package com.pann.gsc.config;

/**
 * Configuration holder for the mod.
 * Can be expanded to support file-based config persistence.
 */
public class ModConfig {
    private static boolean glowEnabledByDefault = false;
    private static boolean fakeStatsEnabledByDefault = false;

    /**
     * Check if glow should be enabled by default.
     * 
     * @return true if glow is enabled by default
     */
    public static boolean isGlowEnabledByDefault() {
        return glowEnabledByDefault;
    }

    /**
     * Set whether glow should be enabled by default.
     * 
     * @param enabled the default state
     */
    public static void setGlowEnabledByDefault(boolean enabled) {
        glowEnabledByDefault = enabled;
    }

    /**
     * Check if fake stats should be enabled by default.
     * 
     * @return true if fake stats are enabled by default
     */
    public static boolean isFakeStatsEnabledByDefault() {
        return fakeStatsEnabledByDefault;
    }

    /**
     * Set whether fake stats should be enabled by default.
     * 
     * @param enabled the default state
     */
    public static void setFakeStatsEnabledByDefault(boolean enabled) {
        fakeStatsEnabledByDefault = enabled;
    }
}
