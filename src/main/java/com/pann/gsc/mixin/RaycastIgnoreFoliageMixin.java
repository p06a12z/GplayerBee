package com.pann.gsc.mixin;

import com.pann.gsc.client.IgnoreFoliageManager;
import net.minecraft.block.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.ArrayList;
import java.util.List;

/**
 * Mixin to allow attacking through grass and cobwebs (Fabric version).
 */
@Mixin(GameRenderer.class)
public class RaycastIgnoreFoliageMixin {

    @ModifyVariable(
            method = "findCrosshairTarget",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/entity/Entity;raycast(DFZ)Lnet/minecraft/util/hit/HitResult;"
            ),
            ordinal = 0
    )
    private HitResult modifyHitResult(HitResult original, Entity camera, double blockInteractionRange,
                                      double entityInteractionRange, float tickDelta) {
        if (!IgnoreFoliageManager.isEnabled()) {
            return original;
        }

        MinecraftClient minecraft = MinecraftClient.getInstance();
        PlayerEntity player = minecraft.player;

        // Check if we hit a block and it's grass/cobweb
        if (original != null && player != null && original.getType() == HitResult.Type.BLOCK) {
            Vec3d hitLocation = original.getPos();
            BlockPos blockPos = BlockPos.ofFloored(hitLocation);

            // Check if block has no collision (grass, cobweb)
            BlockState state = player.getWorld().getBlockState(blockPos);
            if (gsc$isIgnorableBlock(state.getBlock()) &&
                    state.getCollisionShape(player.getWorld(), blockPos).isEmpty()) {

                // Try to find an entity behind the grass/cobweb
                Vec3d start = player.getEyePos();
                Vec3d direction = player.getRotationVec(1.0f);
                Vec3d end = start.add(direction.multiply(entityInteractionRange));

                // Raycast for entities
                EntityHitResult entityHit = ProjectileUtil.raycast(
                        player,
                        start,
                        end,
                        new Box(start, end).expand(1.0),
                        entity -> !entity.isSpectator() &&
                                entity.isAttackable() &&
                                !gsc$isPlayerVehicle(player, entity),
                        entityInteractionRange * entityInteractionRange
                );

                if (entityHit != null) {
                    double enemyDistance = player.distanceTo(entityHit.getEntity());

                    // Check if there's a solid block between player and entity
                    HitResult solidBlockCheck = gsc$raycastSolidBlocks(player, enemyDistance);

                    // If no solid blocks, use entity hit instead
                    if (solidBlockCheck.getType() == HitResult.Type.MISS) {
                        return entityHit;
                    }
                }
            }
        }

        return original;
    }

    /**
     * Check if block should be ignored (grass, cobweb).
     */
    @Unique
    private boolean gsc$isIgnorableBlock(Block block) {
        return block instanceof PlantBlock ||       // Grass, ferns
                block instanceof TallPlantBlock ||   // Tall grass
                block instanceof CobwebBlock;        // Cobweb (tơ nhện)
    }

    /**
     * Check if entity is player's vehicle.
     */
    @Unique
    private boolean gsc$isPlayerVehicle(PlayerEntity player, Entity entity) {
        List<Entity> vehicles = gsc$getAllVehicles(player);
        return vehicles.contains(entity);
    }

    /**
     * Get all vehicles the player is riding.
     */
    @Unique
    private List<Entity> gsc$getAllVehicles(PlayerEntity player) {
        List<Entity> allVehicles = new ArrayList<>();
        Entity vehicleEntity = player.getVehicle();
        while (vehicleEntity != null) {
            allVehicles.add(vehicleEntity);
            vehicleEntity = vehicleEntity.getVehicle();
        }
        return allVehicles;
    }

    /**
     * Raycast for solid blocks only (ignore grass/cobweb).
     */
    @Unique
    private HitResult gsc$raycastSolidBlocks(PlayerEntity player, double reach) {
        Vec3d viewVector = player.getRotationVec(1.0f);
        Vec3d pos = player.getEyePos();
        Vec3d endPos = pos.add(viewVector.multiply(reach));

        return player.getWorld().raycast(new RaycastContext(
                pos,
                endPos,
                RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE,
                player
        ));
    }
}