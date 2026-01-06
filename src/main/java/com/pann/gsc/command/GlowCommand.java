package com.pann.gsc.command;

import com.mojang.brigadier.CommandDispatcher;
import com.pann.gsc.client.GlowManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class GlowCommand {

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(
                literal("glow")
                        .then(literal("on")
                                .executes(context -> {
                                    GlowManager.enableGlow();
                                    context.getSource().sendFeedback(
                                            Text.literal("§aGlow effect enabled for all players!"));
                                    context.getSource().sendFeedback(
                                            Text.literal("§7Friends will glow §agreen§7, others will glow §fwhite§7."));
                                    return 1;
                                }))
                        .then(literal("off")
                                .executes(context -> {
                                    GlowManager.disableGlow();
                                    context.getSource().sendFeedback(
                                            Text.literal("§cGlow effect disabled."));
                                    return 1;
                                }))
                        .then(literal("toggle")
                                .executes(context -> {
                                    boolean newState = GlowManager.toggleGlow();
                                    if (newState) {
                                        context.getSource().sendFeedback(
                                                Text.literal("§aGlow effect enabled for all players!"));
                                        context.getSource().sendFeedback(
                                                Text.literal("§7Friends will glow §agreen§7, others will glow §fwhite§7."));
                                    } else {
                                        context.getSource().sendFeedback(
                                                Text.literal("§cGlow effect disabled."));
                                    }
                                    return 1;
                                }))
                        .executes(context -> {
                            boolean currentState = GlowManager.isGlowEnabled();

                            context.getSource().sendFeedback(
                                    Text.literal("§e=== Glow Status ==="));
                            context.getSource().sendFeedback(
                                    Text.literal("§7Glow: " + (currentState ? "§aEnabled" : "§cDisabled")));
                            context.getSource().sendFeedback(
                                    Text.literal(""));
                            context.getSource().sendFeedback(
                                    Text.literal("§7Usage:"));
                            context.getSource().sendFeedback(
                                    Text.literal("§7  /glow <on | off | toggle>"));
                            context.getSource().sendFeedback(
                                    Text.literal(""));
                            context.getSource().sendFeedback(
                                    Text.literal("§7Manage friends with §e/friends §7command"));
                            return 1;
                        }));
    }
}