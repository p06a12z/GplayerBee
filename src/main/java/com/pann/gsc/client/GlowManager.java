package com.pann.gsc.client;

import net.minecraft.entity.player.PlayerEntity;

public class GlowManager {
    private static boolean glowEnabled = false;

    public static void enableGlow() {
        glowEnabled = true;
    }

    public static void disableGlow() {
        glowEnabled = false;
    }

    public static boolean toggleGlow() {
        glowEnabled = !glowEnabled;
        return glowEnabled;
    }

    public static boolean isGlowEnabled() {
        return glowEnabled;
    }

    public static boolean shouldPlayerGlow(PlayerEntity player) {
        return glowEnabled;
    }

    public static boolean isFriend(PlayerEntity player) {
        return FriendsManager.isFriend(player);
    }
}