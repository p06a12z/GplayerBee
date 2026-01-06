package com.pann.gsc.client;

/**
 * Manages the ignore foliage state for combat.
 * When enabled, allows attacking through grass, flowers, and other foliage.
 */
public class IgnoreFoliageManager {
    private static boolean ignoreFoliageEnabled = false;

    /**
     * Enable ignore foliage (attack through grass/flowers).
     */
    public static void enable() {
        ignoreFoliageEnabled = true;
    }

    /**
     * Disable ignore foliage.
     */
    public static void disable() {
        ignoreFoliageEnabled = false;
    }

    /**
     * Toggle ignore foliage state.
     *
     * @return the new state after toggling
     */
    public static boolean toggle() {
        ignoreFoliageEnabled = !ignoreFoliageEnabled;
        return ignoreFoliageEnabled;
    }

    /**
     * Check if ignore foliage is currently enabled.
     *
     * @return true if ignore foliage is enabled
     */
    public static boolean isEnabled() {
        return ignoreFoliageEnabled;
    }
}