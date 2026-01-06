package com.pann.gsc.command;

import com.mojang.brigadier.CommandDispatcher;
import com.pann.gsc.client.IgnoreFoliageManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

/**
 * Client-side command handler for ignore foliage feature.
 * Commands:
 * /ignorefoliage on - Enable attacking through grass/flowers
 * /ignorefoliage off - Disable
 * /ignorefoliage toggle - Toggle state
 */
public class IgnoreFoliageCommand {

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(
                literal("ignorefoliage")
                        .then(literal("on")
                                .executes(context -> {
                                    IgnoreFoliageManager.enable();
                                    context.getSource().sendFeedback(
                                            Text.literal("§aIgnore Foliage enabled!"));
                                    context.getSource().sendFeedback(
                                            Text.literal("§7You can now attack through grass, flowers, and plants."));
                                    return 1;
                                }))
                        .then(literal("off")
                                .executes(context -> {
                                    IgnoreFoliageManager.disable();
                                    context.getSource().sendFeedback(
                                            Text.literal("§cIgnore Foliage disabled."));
                                    return 1;
                                }))
                        .then(literal("toggle")
                                .executes(context -> {
                                    boolean newState = IgnoreFoliageManager.toggle();
                                    if (newState) {
                                        context.getSource().sendFeedback(
                                                Text.literal("§aIgnore Foliage enabled!"));
                                        context.getSource().sendFeedback(
                                                Text.literal("§7You can now attack through grass, flowers, and plants."));
                                    } else {
                                        context.getSource().sendFeedback(
                                                Text.literal("§cIgnore Foliage disabled."));
                                    }
                                    return 1;
                                }))
                        .executes(context -> {
                            boolean enabled = IgnoreFoliageManager.isEnabled();
                            context.getSource().sendFeedback(
                                    Text.literal("§e=== Ignore Foliage ==="));
                            context.getSource().sendFeedback(
                                    Text.literal("§7Status: " + (enabled ? "§aEnabled" : "§cDisabled")));
                            context.getSource().sendFeedback(
                                    Text.literal("§7When enabled, you can attack through:"));
                            context.getSource().sendFeedback(
                                    Text.literal("§7  • Grass, tall grass, ferns"));
                            context.getSource().sendFeedback(
                                    Text.literal("§7  • Flowers, tall flowers"));
                            context.getSource().sendFeedback(
                                    Text.literal("§7  • Vines, sugar cane, seagrass"));
                            context.getSource().sendFeedback(
                                    Text.literal("§7  • Crops, mushrooms, saplings"));
                            context.getSource().sendFeedback(
                                    Text.literal(""));
                            context.getSource().sendFeedback(
                                    Text.literal("§7Usage: /ignorefoliage <on|off|toggle>"));
                            return 1;
                        }));

        // Short alias
        dispatcher.register(
                literal("if")
                        .redirect(dispatcher.getRoot().getChild("ignorefoliage"))
        );
    }
}