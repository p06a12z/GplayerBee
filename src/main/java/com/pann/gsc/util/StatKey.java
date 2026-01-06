package com.pann.gsc.util;

import net.minecraft.util.Identifier;

import java.util.Objects;

/**
 * A key for identifying a specific statistic.
 * Combines the stat type (mined, broken, crafted, used, picked_up,
 * dropped) with the block/item identifier.
 */
public class StatKey {
    private final String type;
    private final Identifier itemId;

    /**
     * Create a new StatKey.
     *
     * @param type   the type of stat (mined, broken, crafted, used, picked_up,
     *               dropped)
     * @param itemId the identifier of the block/item
     */
    public StatKey(String type, Identifier itemId) {
        this.type = type.toLowerCase();
        this.itemId = itemId;
    }

    /**
     * Get the stat type.
     *
     * @return the stat type
     */
    public String getType() {
        return type;
    }

    /**
     * Get the item/block identifier.
     *
     * @return the identifier
     */
    public Identifier getItemId() {
        return itemId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        StatKey statKey = (StatKey) o;
        return Objects.equals(type, statKey.type) && Objects.equals(itemId, statKey.itemId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, itemId);
    }

    @Override
    public String toString() {
        return "StatKey{type='" + type + "', itemId=" + itemId + "}";
    }

    /**
     * Validate if the type is a valid stat type.
     *
     * @param type the type to validate
     * @return true if the type is valid
     */
    public static boolean isValidType(String type) {
        String lowerType = type.toLowerCase();
        return lowerType.equals("mined") ||
                lowerType.equals("broken") ||
                lowerType.equals("crafted") ||
                lowerType.equals("used") ||
                lowerType.equals("picked_up") ||
                lowerType.equals("dropped") ||
                lowerType.equals("killed") ||
                lowerType.equals("killed_by");
    }

    /**
     * Get all valid stat types.
     *
     * @return array of valid stat types
     */
    public static String[] getValidTypes() {
        return new String[] { "mined", "broken", "crafted", "used", "picked_up", "dropped", "killed", "killed_by" };
    }
}