package com.pann.gsc.client;

import net.minecraft.entity.player.PlayerEntity;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Manages the friends list (client-side only).
 * Friends will:
 * - Glow green when glow is enabled
 * - Not be targeted by auto attack
 */
public class FriendsManager {
    private static final Set<String> friends = new HashSet<>();
    private static final Set<UUID> friendUUIDs = new HashSet<>();

    /**
     * Add a friend by username.
     */
    public static void addFriend(String username) {
        friends.add(username.toLowerCase());
    }

    /**
     * Add a friend by UUID.
     */
    public static void addFriendByUUID(UUID uuid) {
        friendUUIDs.add(uuid);
    }

    /**
     * Remove a friend by username.
     */
    public static boolean removeFriend(String username) {
        return friends.remove(username.toLowerCase());
    }

    /**
     * Remove a friend by UUID.
     */
    public static boolean removeFriendByUUID(UUID uuid) {
        return friendUUIDs.remove(uuid);
    }

    /**
     * Check if a player is in the friends list.
     */
    public static boolean isFriend(PlayerEntity player) {
        if (player == null) return false;

        // Check by UUID first (more reliable)
        if (friendUUIDs.contains(player.getUuid())) {
            return true;
        }

        // Check by username
        String playerName = player.getName().getString().toLowerCase();
        return friends.contains(playerName);
    }

    /**
     * Check if a username is in the friends list.
     */
    public static boolean isFriend(String username) {
        return friends.contains(username.toLowerCase());
    }

    /**
     * Clear all friends.
     */
    public static void clearFriends() {
        friends.clear();
        friendUUIDs.clear();
    }

    /**
     * Get all friends usernames.
     */
    public static Set<String> getAllFriends() {
        return new HashSet<>(friends);
    }

    /**
     * Get friend count.
     */
    public static int getFriendCount() {
        return friends.size() + friendUUIDs.size();
    }
}