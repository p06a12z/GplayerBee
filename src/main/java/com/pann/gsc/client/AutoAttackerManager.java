package com.pann.gsc.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import org.lwjgl.glfw.GLFW;

public class AutoAttackerManager {
    private static boolean enabled = false;
    private static int boundKey = GLFW.GLFW_KEY_R;
    private static String boundKeyName = "R";
    private static boolean keyWasPressed = false;
    private static boolean autoCrit = true;
    private static boolean waitingForCrit = false;
    private static int ticksSinceJump = 0;
    private static boolean autoShieldBreak = true;
    private static int originalSlot = -1;
    private static boolean switchedToAxe = false;
    private static final float ATTACK_COOLDOWN_THRESHOLD = 0.9f;

    public static void enable() {
        enabled = true;
        reset();
    }

    public static void disable() {
        enabled = false;
        restoreOriginalSlot();
        reset();
    }

    public static boolean toggle() {
        if (enabled) {
            disable();
        } else {
            enable();
        }
        return enabled;
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static void setAutoCrit(boolean value) {
        autoCrit = value;
    }

    public static boolean isAutoCrit() {
        return autoCrit;
    }

    public static void setAutoShieldBreak(boolean value) {
        autoShieldBreak = value;
    }

    public static boolean isAutoShieldBreak() {
        return autoShieldBreak;
    }

    public static boolean setKey(String keyName) {
        keyName = keyName.toUpperCase();
        int keyCode = getKeyCode(keyName);

        if (keyCode == GLFW.GLFW_KEY_UNKNOWN) {
            return false;
        }

        boundKey = keyCode;
        boundKeyName = keyName;
        return true;
    }

    public static String getBoundKeyName() {
        return boundKeyName;
    }

    public static int getBoundKey() {
        return boundKey;
    }

    private static int getKeyCode(String keyName) {
        return switch (keyName) {
            case "R" -> GLFW.GLFW_KEY_R;
            case "G" -> GLFW.GLFW_KEY_G;
            case "V" -> GLFW.GLFW_KEY_V;
            case "C" -> GLFW.GLFW_KEY_C;
            case "X" -> GLFW.GLFW_KEY_X;
            case "Z" -> GLFW.GLFW_KEY_Z;
            case "B" -> GLFW.GLFW_KEY_B;
            case "N" -> GLFW.GLFW_KEY_N;
            case "M" -> GLFW.GLFW_KEY_M;
            case "H" -> GLFW.GLFW_KEY_H;
            case "J" -> GLFW.GLFW_KEY_J;
            case "K" -> GLFW.GLFW_KEY_K;
            case "L" -> GLFW.GLFW_KEY_L;
            case "F" -> GLFW.GLFW_KEY_F;
            case "T" -> GLFW.GLFW_KEY_T;
            case "Y" -> GLFW.GLFW_KEY_Y;
            case "U" -> GLFW.GLFW_KEY_U;
            case "I" -> GLFW.GLFW_KEY_I;
            case "O" -> GLFW.GLFW_KEY_O;
            case "P" -> GLFW.GLFW_KEY_P;
            default -> GLFW.GLFW_KEY_UNKNOWN;
        };
    }

    public static boolean isKeyPressed() {
        MinecraftClient client = MinecraftClient.getInstance();
        long window = client.getWindow().getHandle();
        return GLFW.glfwGetKey(window, boundKey) == GLFW.GLFW_PRESS;
    }

    public static void tick() {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;

        if (player == null || client.currentScreen != null || player.isSpectator()) {
            return;
        }

        boolean keyPressed = isKeyPressed();
        if (keyPressed && !keyWasPressed) {
            toggle();
            if (enabled) {
                player.sendMessage(net.minecraft.text.Text.literal("§aAuto Attack: §eON"), true);
            } else {
                player.sendMessage(net.minecraft.text.Text.literal("§cAuto Attack: §eOFF"), true);
            }
        }
        keyWasPressed = keyPressed;

        if (!enabled) {
            restoreOriginalSlot();
            waitingForCrit = false;
            ticksSinceJump = 0;
            return;
        }

        if (autoCrit) {
            if (!player.isOnGround()) {
                ticksSinceJump++;
            } else {
                ticksSinceJump = 0;
                waitingForCrit = false;
            }
        }

        if (player.getAttackCooldownProgress(0.5f) >= ATTACK_COOLDOWN_THRESHOLD) {
            tryAttack(client);
        }
    }

    private static void tryAttack(MinecraftClient client) {
        if (client.crosshairTarget == null) {
            return;
        }

        if (client.crosshairTarget.getType() != HitResult.Type.ENTITY) {
            return;
        }

        EntityHitResult entityHit = (EntityHitResult) client.crosshairTarget;
        Entity target = entityHit.getEntity();
        ClientPlayerEntity player = client.player;

        if (!shouldAttack(target, player)) {
            return;
        }

        if (autoCrit && !player.isOnGround()) {
            if (player.getVelocity().y < 0 && ticksSinceJump >= 2) {
                waitingForCrit = false;
            } else {
                waitingForCrit = true;
                return;
            }
        }

        if (autoShieldBreak && target instanceof LivingEntity livingTarget) {
            if (isShielding(livingTarget)) {
                if (!switchedToAxe) {
                    int axeSlot = findAxeSlot(player);
                    if (axeSlot != -1 && axeSlot != player.getInventory().selectedSlot) {
                        originalSlot = player.getInventory().selectedSlot;
                        player.getInventory().selectedSlot = axeSlot;
                        switchedToAxe = true;
                    }
                }
            } else {
                restoreOriginalSlot();
            }
        }

        client.interactionManager.attackEntity(player, target);
        player.swingHand(Hand.MAIN_HAND);
        player.resetLastAttackedTicks();
    }

    private static boolean shouldAttack(Entity entity, ClientPlayerEntity player) {
        if (player.isUsingItem() && player.getActiveItem().getItem() instanceof ShieldItem) {
            return false;
        }

        // QUAN TRỌNG: Không tấn công friends!
        if (entity instanceof PlayerEntity targetPlayer) {
            if (FriendsManager.isFriend(targetPlayer)) {
                return false;
            }
            return true;
        }

        if (entity instanceof ArmorStandEntity) {
            ItemStack mainHand = player.getMainHandStack();
            return isWeapon(mainHand);
        }

        if (!(entity instanceof LivingEntity)) {
            return false;
        }

        if (entity instanceof TameableEntity tameable && tameable.isTamed()) {
            return false;
        }

        return entity instanceof HostileEntity || entity instanceof LivingEntity;
    }

    private static boolean isShielding(LivingEntity entity) {
        return entity.isUsingItem() && entity.getActiveItem().getItem() instanceof ShieldItem;
    }

    private static boolean isWeapon(ItemStack stack) {
        Item item = stack.getItem();
        return stack.isIn(ItemTags.SWORDS) ||
                stack.isIn(ItemTags.AXES) ||
                item instanceof SwordItem ||
                item instanceof AxeItem;
    }

    private static int findAxeSlot(ClientPlayerEntity player) {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.getItem() instanceof AxeItem) {
                return i;
            }
        }
        return -1;
    }

    private static void restoreOriginalSlot() {
        if (switchedToAxe && originalSlot != -1) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player != null) {
                client.player.getInventory().selectedSlot = originalSlot;
            }
            originalSlot = -1;
            switchedToAxe = false;
        }
    }

    public static void reset() {
        waitingForCrit = false;
        ticksSinceJump = 0;
        restoreOriginalSlot();
        keyWasPressed = false;
    }
}