package com.pann.gsc.client;

import com.pann.gsc.util.StatKey;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages fake statistics for client-side display.
 * These fake stats only affect what the client sees and do not modify server
 * data.
 */
public class FakeStatManager {
    private static final Map<StatKey, Integer> fakeStats = new HashMap<>();
    private static boolean fakeStatsEnabled = false;

    /**
     * Set a fake stat value.
     * 
     * @param type   the type of stat (mined, broken, crafted, used, picked_up,
     *               dropped)
     * @param itemId the identifier of the block/item
     * @param value  the fake value to display
     */
    public static void setFakeStat(String type, Identifier itemId, int value) {
        StatKey key = new StatKey(type, itemId);
        fakeStats.put(key, value);
    }

    /**
     * Remove a specific fake stat.
     * 
     * @param type   the type of stat
     * @param itemId the identifier of the block/item
     */
    public static void removeFakeStat(String type, Identifier itemId) {
        StatKey key = new StatKey(type, itemId);
        fakeStats.remove(key);
    }

    /**
     * Reset all fake stats.
     */
    public static void resetAllFakeStats() {
        fakeStats.clear();
    }

    /**
     * Get a fake stat value if it exists.
     * 
     * @param type   the type of stat
     * @param itemId the identifier of the block/item
     * @return the fake value, or null if no fake stat is set
     */
    public static Integer getFakeStat(String type, Identifier itemId) {
        if (!fakeStatsEnabled) {
            return null;
        }
        StatKey key = new StatKey(type, itemId);
        return fakeStats.get(key);
    }

    /**
     * Check if a fake stat exists for the given type and item.
     * 
     * @param type   the type of stat
     * @param itemId the identifier of the block/item
     * @return true if a fake stat exists
     */
    public static boolean hasFakeStat(String type, Identifier itemId) {
        if (!fakeStatsEnabled) {
            return false;
        }
        StatKey key = new StatKey(type, itemId);
        return fakeStats.containsKey(key);
    }

    /**
     * Enable fake stats display.
     */
    public static void enableFakeStats() {
        fakeStatsEnabled = true;
    }

    /**
     * Disable fake stats display.
     */
    public static void disableFakeStats() {
        fakeStatsEnabled = false;
    }

    /**
     * Toggle fake stats state.
     * 
     * @return the new state after toggling
     */
    public static boolean toggleFakeStats() {
        fakeStatsEnabled = !fakeStatsEnabled;
        return fakeStatsEnabled;
    }

    /**
     * Check if fake stats are currently enabled.
     * 
     * @return true if fake stats are enabled
     */
    public static boolean isFakeStatsEnabled() {
        return fakeStatsEnabled;
    }

    /**
     * Get all current fake stats.
     * 
     * @return a copy of the fake stats map
     */
    public static Map<StatKey, Integer> getAllFakeStats() {
        return new HashMap<>(fakeStats);
    }

    /**
     * Get the count of fake stats currently set.
     * 
     * @return number of fake stats
     */
    public static int getFakeStatCount() {
        return fakeStats.size();
    }
}
