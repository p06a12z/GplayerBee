package com.pann.gsc.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.pann.gsc.client.AutoAttackerManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class AutoAttackCommand {

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(
                literal("autoattack")
                        .then(literal("on")
                                .executes(context -> {
                                    AutoAttackerManager.enable();
                                    context.getSource().sendFeedback(
                                            Text.literal("§aAuto Attack enabled! Hold §e" +
                                                    AutoAttackerManager.getBoundKeyName() +
                                                    "§a to auto attack."));
                                    return 1;
                                }))
                        .then(literal("off")
                                .executes(context -> {
                                    AutoAttackerManager.disable();
                                    context.getSource().sendFeedback(
                                            Text.literal("§cAuto Attack disabled."));
                                    return 1;
                                }))
                        .then(literal("toggle")
                                .executes(context -> {
                                    boolean newState = AutoAttackerManager.toggle();
                                    context.getSource().sendFeedback(
                                            newState
                                                    ? Text.literal("§aAuto Attack enabled! Hold §e" +
                                                    AutoAttackerManager.getBoundKeyName())
                                                    : Text.literal("§cAuto Attack disabled.")
                                    );
                                    return 1;
                                }))
                        .then(literal("key")
                                .then(argument("keyname", StringArgumentType.word())
                                        .executes(context -> {
                                            String keyName = StringArgumentType.getString(context, "keyname");

                                            if (AutoAttackerManager.setKey(keyName)) {
                                                context.getSource().sendFeedback(
                                                        Text.literal("§aSet auto attack key to: §e" +
                                                                AutoAttackerManager.getBoundKeyName()));
                                            } else {
                                                context.getSource().sendFeedback(
                                                        Text.literal("§cInvalid key! Valid keys: R, G, V, C, X, Z, B, N, M, H, J, K, L, F, T, Y, U, I, O, P"));
                                            }
                                            return 1;
                                        }))
                                .executes(context -> {
                                    context.getSource().sendFeedback(
                                            Text.literal("§eCurrent key: §b" + AutoAttackerManager.getBoundKeyName()));
                                    return 1;
                                }))
                        .then(literal("autocrit")
                                .then(argument("enabled", BoolArgumentType.bool())
                                        .executes(context -> {
                                            boolean enabled = BoolArgumentType.getBool(context, "enabled");
                                            AutoAttackerManager.setAutoCrit(enabled);
                                            context.getSource().sendFeedback(
                                                    Text.literal(enabled
                                                            ? "§aAuto Crit enabled! Will only attack when falling for crits."
                                                            : "§cAuto Crit disabled."));
                                            return 1;
                                        }))
                                .executes(context -> {
                                    boolean enabled = AutoAttackerManager.isAutoCrit();
                                    context.getSource().sendFeedback(
                                            Text.literal("§eAuto Crit: " + (enabled ? "§aEnabled" : "§cDisabled")));
                                    return 1;
                                }))
                        .then(literal("shieldbreak")
                                .then(argument("enabled", BoolArgumentType.bool())
                                        .executes(context -> {
                                            boolean enabled = BoolArgumentType.getBool(context, "enabled");
                                            AutoAttackerManager.setAutoShieldBreak(enabled);
                                            context.getSource().sendFeedback(
                                                    Text.literal(enabled
                                                            ? "§aAuto Shield Break enabled! Will switch to axe when target shields."
                                                            : "§cAuto Shield Break disabled."));
                                            return 1;
                                        }))
                                .executes(context -> {
                                    boolean enabled = AutoAttackerManager.isAutoShieldBreak();
                                    context.getSource().sendFeedback(
                                            Text.literal("§eAuto Shield Break: " + (enabled ? "§aEnabled" : "§cDisabled")));
                                    return 1;
                                }))
                        .executes(context -> {
                            boolean enabled = AutoAttackerManager.isEnabled();
                            boolean autoCrit = AutoAttackerManager.isAutoCrit();
                            boolean shieldBreak = AutoAttackerManager.isAutoShieldBreak();
                            String key = AutoAttackerManager.getBoundKeyName();

                            context.getSource().sendFeedback(
                                    Text.literal("§e=== Auto Attack ==="));
                            context.getSource().sendFeedback(
                                    Text.literal("§7Status: " + (enabled ? "§aEnabled" : "§cDisabled")));
                            context.getSource().sendFeedback(
                                    Text.literal("§7Keybind: §b" + key));
                            context.getSource().sendFeedback(
                                    Text.literal("§7Auto Crit: " + (autoCrit ? "§aOn" : "§cOff")));
                            context.getSource().sendFeedback(
                                    Text.literal("§7Shield Break: " + (shieldBreak ? "§aOn" : "§cOff")));
                            context.getSource().sendFeedback(
                                    Text.literal(""));
                            context.getSource().sendFeedback(
                                    Text.literal("§7Commands:"));
                            context.getSource().sendFeedback(
                                    Text.literal("§7  /autoattack on|off|toggle"));
                            context.getSource().sendFeedback(
                                    Text.literal("§7  /autoattack key <key>"));
                            context.getSource().sendFeedback(
                                    Text.literal("§7  /autoattack autocrit <true|false>"));
                            context.getSource().sendFeedback(
                                    Text.literal("§7  /autoattack shieldbreak <true|false>"));
                            context.getSource().sendFeedback(
                                    Text.literal(""));
                            context.getSource().sendFeedback(
                                    Text.literal("§7Features:"));
                            context.getSource().sendFeedback(
                                    Text.literal("§7  • Auto crit when jumping (waits for fall)"));
                            context.getSource().sendFeedback(
                                    Text.literal("§7  • Auto switch to axe when target shields"));
                            return 1;
                        })
        );

        // Short alias
        dispatcher.register(
                literal("aa")
                        .redirect(dispatcher.getRoot().getChild("autoattack"))
        );
    }
}