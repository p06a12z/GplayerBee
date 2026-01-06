package com.pann.gsc.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.pann.gsc.client.FakeStatManager;
import com.pann.gsc.util.StatKey;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class FakeStatCommand {

    private static final SuggestionProvider<FabricClientCommandSource> TYPE_SUGGESTIONS =
            (context, builder) -> CommandSource.suggestMatching(StatKey.getValidTypes(), builder);

    private static final SuggestionProvider<FabricClientCommandSource> BLOCK_SUGGESTIONS =
            (context, builder) -> CommandSource.suggestIdentifiers(Registries.BLOCK.getIds(), builder);

    private static final SuggestionProvider<FabricClientCommandSource> ITEM_SUGGESTIONS =
            (context, builder) -> CommandSource.suggestIdentifiers(Registries.ITEM.getIds(), builder);

    private static final SuggestionProvider<FabricClientCommandSource> ENTITY_SUGGESTIONS =
            (context, builder) -> CommandSource.suggestIdentifiers(Registries.ENTITY_TYPE.getIds(), builder);

    private static final SuggestionProvider<FabricClientCommandSource> DYNAMIC_ID_SUGGESTIONS = (context, builder) -> {
        String type = null;
        try {
            type = StringArgumentType.getString(context, "type").toLowerCase();
        } catch (IllegalArgumentException e) {
            return Suggestions.empty();
        }

        return switch (type) {
            case "mined" -> BLOCK_SUGGESTIONS.getSuggestions(context, builder);
            case "crafted", "used", "broken", "picked_up", "dropped" -> ITEM_SUGGESTIONS.getSuggestions(context, builder);
            case "killed", "killed_by" -> ENTITY_SUGGESTIONS.getSuggestions(context, builder);
            default -> Suggestions.empty();
        };
    };

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher,
                                CommandRegistryAccess registryAccess) {
        dispatcher.register(
                literal("fakestats")
                        .then(literal("set")
                                .then(argument("type", StringArgumentType.word())
                                        .suggests(TYPE_SUGGESTIONS)
                                        .then(argument("id", StringArgumentType.string())
                                                .suggests(DYNAMIC_ID_SUGGESTIONS)
                                                .then(argument("value", IntegerArgumentType.integer(0))
                                                        .executes(context -> {
                                                            String type = StringArgumentType.getString(context, "type");
                                                            String idString = StringArgumentType.getString(context, "id");
                                                            int value = IntegerArgumentType.getInteger(context, "value");

                                                            if (!StatKey.isValidType(type)) {
                                                                context.getSource().sendFeedback(
                                                                        Text.literal("§cInvalid stat type! Valid types: " + String.join(", ", StatKey.getValidTypes())));
                                                                return 0;
                                                            }

                                                            Identifier itemId = Identifier.tryParse(idString);
                                                            if (itemId == null) {
                                                                context.getSource().sendFeedback(
                                                                        Text.literal("§cInvalid ID: " + idString));
                                                                return 0;
                                                            }

                                                            FakeStatManager.setFakeStat(type, itemId, value);
                                                            context.getSource().sendFeedback(
                                                                    Text.literal("§aSet fake stat: §e" + type + " " + itemId + " §7= §b" + value));
                                                            return 1;
                                                        })))))
                        .then(literal("remove")
                                .then(argument("type", StringArgumentType.word())
                                        .suggests(TYPE_SUGGESTIONS)
                                        .then(argument("id", StringArgumentType.string())
                                                .suggests(DYNAMIC_ID_SUGGESTIONS)
                                                .executes(context -> {
                                                    String type = StringArgumentType.getString(context, "type");
                                                    String idString = StringArgumentType.getString(context, "id");

                                                    if (!StatKey.isValidType(type)) {
                                                        context.getSource().sendFeedback(
                                                                Text.literal("§cInvalid stat type!"));
                                                        return 0;
                                                    }

                                                    Identifier itemId = Identifier.tryParse(idString);
                                                    if (itemId == null) {
                                                        context.getSource().sendFeedback(
                                                                Text.literal("§cInvalid ID: " + idString));
                                                        return 0;
                                                    }

                                                    FakeStatManager.removeFakeStat(type, itemId);
                                                    context.getSource().sendFeedback(
                                                            Text.literal("§aRemoved fake stat: §e" + type + " " + itemId));
                                                    return 1;
                                                }))))
                        .then(literal("reset")
                                .executes(context -> {
                                    int count = FakeStatManager.getFakeStatCount();
                                    FakeStatManager.resetAllFakeStats();
                                    context.getSource().sendFeedback(
                                            Text.literal("§aReset all fake stats! §7(" + count + " stats removed)"));
                                    return 1;
                                }))
                        .then(literal("toggle")
                                .executes(context -> {
                                    boolean newState = FakeStatManager.toggleFakeStats();
                                    context.getSource().sendFeedback(
                                            newState
                                                    ? Text.literal("§aFake stats enabled!")
                                                    : Text.literal("§cFake stats disabled.")
                                    );
                                    return 1;
                                }))
                        .then(literal("list")
                                .executes(context -> {
                                    var stats = FakeStatManager.getAllFakeStats();
                                    if (stats.isEmpty()) {
                                        context.getSource().sendFeedback(Text.literal("§7No fake stats set."));
                                    } else {
                                        context.getSource().sendFeedback(Text.literal("§e=== Fake Stats (" + stats.size() + ") ==="));
                                        stats.forEach((key, value) ->
                                                context.getSource().sendFeedback(
                                                        Text.literal("§7- §e" + key.getType() + " §f" + key.getItemId() + " §7= §b" + value)
                                                )
                                        );
                                    }
                                    return 1;
                                }))
                        .executes(context -> {
                            boolean enabled = FakeStatManager.isFakeStatsEnabled();
                            int count = FakeStatManager.getFakeStatCount();
                            context.getSource().sendFeedback(
                                    Text.literal("§eFake Stats Status: " +
                                            (enabled ? "§aEnabled" : "§cDisabled") +
                                            " §7(" + count + " stats set)"));
                            context.getSource().sendFeedback(Text.literal("§7Usage:"));
                            context.getSource().sendFeedback(Text.literal("§7  /fakestats set <type> <id> <value>"));
                            context.getSource().sendFeedback(Text.literal("§7  /fakestats remove <type> <id>"));
                            context.getSource().sendFeedback(Text.literal("§7  /fakestats reset | toggle | list"));
                            return 1;
                        })
        );
    }
}