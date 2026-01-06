package com.pann.gsc.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.pann.gsc.client.FriendsManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class FriendsCommand {

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(
                literal("friends")
                        .then(literal("add")
                                .then(argument("username", StringArgumentType.word())
                                        .executes(context -> {
                                            String username = StringArgumentType.getString(context, "username");
                                            FriendsManager.addFriend(username);
                                            context.getSource().sendFeedback(
                                                    Text.literal("§aAdded §e" + username + " §ato friends!"));
                                            context.getSource().sendFeedback(
                                                    Text.literal("§7They will glow §agreen §7and won't be auto attacked."));
                                            return 1;
                                        })))
                        .then(literal("remove")
                                .then(argument("username", StringArgumentType.word())
                                        .executes(context -> {
                                            String username = StringArgumentType.getString(context, "username");
                                            if (FriendsManager.removeFriend(username)) {
                                                context.getSource().sendFeedback(
                                                        Text.literal("§cRemoved §e" + username + " §cfrom friends."));
                                            } else {
                                                context.getSource().sendFeedback(
                                                        Text.literal("§c" + username + " is not in your friends list!"));
                                            }
                                            return 1;
                                        })))
                        .then(literal("list")
                                .executes(context -> {
                                    var friendsList = FriendsManager.getAllFriends();
                                    int count = FriendsManager.getFriendCount();

                                    if (friendsList.isEmpty()) {
                                        context.getSource().sendFeedback(
                                                Text.literal("§7You have no friends added."));
                                        context.getSource().sendFeedback(
                                                Text.literal("§7Use §e/friends add <username> §7to add friends."));
                                    } else {
                                        context.getSource().sendFeedback(
                                                Text.literal("§e=== Friends List (" + count + ") ==="));
                                        friendsList.forEach(friend ->
                                                context.getSource().sendFeedback(
                                                        Text.literal("§a• §f" + friend))
                                        );
                                    }
                                    return 1;
                                }))
                        .then(literal("clear")
                                .executes(context -> {
                                    int count = FriendsManager.getFriendCount();
                                    FriendsManager.clearFriends();
                                    context.getSource().sendFeedback(
                                            Text.literal("§cCleared all friends! §7(" + count + " removed)"));
                                    return 1;
                                }))
                        .executes(context -> {
                            int count = FriendsManager.getFriendCount();
                            context.getSource().sendFeedback(
                                    Text.literal("§e=== Friends Management ==="));
                            context.getSource().sendFeedback(
                                    Text.literal("§7Friends: §b" + count));
                            context.getSource().sendFeedback(
                                    Text.literal(""));
                            context.getSource().sendFeedback(
                                    Text.literal("§7Commands:"));
                            context.getSource().sendFeedback(
                                    Text.literal("§7  /friends add <username>"));
                            context.getSource().sendFeedback(
                                    Text.literal("§7  /friends remove <username>"));
                            context.getSource().sendFeedback(
                                    Text.literal("§7  /friends list"));
                            context.getSource().sendFeedback(
                                    Text.literal("§7  /friends clear"));
                            context.getSource().sendFeedback(
                                    Text.literal(""));
                            context.getSource().sendFeedback(
                                    Text.literal("§7Features:"));
                            context.getSource().sendFeedback(
                                    Text.literal("§7  • Friends glow §agreen §7when glow is enabled"));
                            context.getSource().sendFeedback(
                                    Text.literal("§7  • Auto attack won't target friends"));
                            return 1;
                        }));

        dispatcher.register(
                literal("friend")
                        .redirect(dispatcher.getRoot().getChild("friends"))
        );
    }
}