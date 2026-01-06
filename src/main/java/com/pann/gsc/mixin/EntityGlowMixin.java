package com.pann.gsc.mixin;

import com.pann.gsc.client.GlowManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin to make players glow on the client side.
 * Friends glow green, non-friends glow white.
 */
@Mixin(Entity.class)
public abstract class EntityGlowMixin {

    @Inject(method = "isGlowing", at = @At("HEAD"), cancellable = true)
    private void onIsGlowing(CallbackInfoReturnable<Boolean> cir) {
        Entity self = (Entity) (Object) this;

        // Only apply to players
        if (self instanceof PlayerEntity player) {
            // Don't apply to the local player viewing themselves
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player != null && self != client.player) {
                if (GlowManager.shouldPlayerGlow(player)) {
                    cir.setReturnValue(true);
                }
            }
        }
    }

    /**
     * Override glow team color.
     * Friends = green (dark_green), non-friends = white.
     */
    @Inject(method = "getTeamColorValue", at = @At("HEAD"), cancellable = true)
    private void onGetTeamColorValue(CallbackInfoReturnable<Integer> cir) {
        Entity self = (Entity) (Object) this;

        if (self instanceof PlayerEntity player) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player != null && self != client.player) {
                if (GlowManager.shouldPlayerGlow(player)) {
                    if (GlowManager.isFriend(player)) {
                        // Green color for friends (0x55FF55)
                        cir.setReturnValue(0x55FF55);
                    } else {
                        // White color for non-friends (0xFFFFFF)
                        cir.setReturnValue(0xFFFFFF);
                    }
                }
            }
        }
    }
}