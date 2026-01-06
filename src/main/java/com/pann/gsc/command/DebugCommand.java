package com.pann.gsc.command;

import com.mojang.brigadier.CommandDispatcher;
import com.pann.gsc.GSC;
import com.pann.gsc.client.FakeStatManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.stat.StatHandler;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class DebugCommand {

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(
                literal("fsdebug")
                        .executes(context -> {
                            context.getSource().sendFeedback(Text.literal("§e=== Fake Stats Debug Info ==="));

                            // 1. Check if fake stats is enabled
                            boolean enabled = FakeStatManager.isFakeStatsEnabled();
                            context.getSource().sendFeedback(
                                    Text.literal("§7Fake Stats Enabled: " + (enabled ? "§aYES" : "§cNO")));
                            GSC.LOGGER.info("=== FAKE STATS DEBUG ===");
                            GSC.LOGGER.info("Fake Stats Enabled: {}", enabled);

                            // 2. Check how many fake stats are stored
                            int count = FakeStatManager.getFakeStatCount();
                            context.getSource().sendFeedback(
                                    Text.literal("§7Fake Stats Count: §b" + count));
                            GSC.LOGGER.info("Fake Stats Count: {}", count);

                            // 3. List all fake stats
                            if (count > 0) {
                                context.getSource().sendFeedback(Text.literal("§7Stored Fake Stats:"));
                                FakeStatManager.getAllFakeStats().forEach((key, value) -> {
                                    String msg = "  - " + key.getType() + " " + key.getItemId() + " = " + value;
                                    context.getSource().sendFeedback(Text.literal("§7" + msg));
                                    GSC.LOGGER.info(msg);
                                });
                            }

                            // 4. Test reading a real stat
                            MinecraftClient client = MinecraftClient.getInstance();
                            if (client.player != null) {
                                StatHandler statHandler = client.player.getStatHandler();

                                // Test reading stone mined stat
                                Identifier stoneId = Identifier.ofVanilla("stone");
                                int realStoneMined = statHandler.getStat(Stats.MINED.getOrCreateStat(
                                        net.minecraft.registry.Registries.BLOCK.get(stoneId)));

                                context.getSource().sendFeedback(
                                        Text.literal("§7Real Stone Mined: §b" + realStoneMined));
                                GSC.LOGGER.info("Real Stone Mined stat: {}", realStoneMined);

                                // Check if we have a fake stat for stone
                                Integer fakeStoneMined = FakeStatManager.getFakeStat("mined", stoneId);
                                if (fakeStoneMined != null) {
                                    context.getSource().sendFeedback(
                                            Text.literal("§7Fake Stone Mined: §b" + fakeStoneMined));
                                    GSC.LOGGER.info("Fake Stone Mined stat: {}", fakeStoneMined);
                                } else {
                                    context.getSource().sendFeedback(
                                            Text.literal("§7Fake Stone Mined: §cNOT SET"));
                                    GSC.LOGGER.info("No fake stone mined stat set");
                                }
                            }

                            // 5. Test mixin status
                            context.getSource().sendFeedback(
                                    Text.literal("§7Check console for detailed logs!"));
                            GSC.LOGGER.info("=== Testing Mixin ===");
                            GSC.LOGGER.info("If you see 'StatHandlerMixin is working!' in logs, mixin is loaded.");
                            GSC.LOGGER.info("If not, mixin is NOT loaded or NOT applied!");
                            GSC.LOGGER.info("=== END DEBUG ===");

                            return 1;
                        })
                        .then(literal("test")
                                .executes(context -> {
                                    context.getSource().sendFeedback(
                                            Text.literal("§e=== Testing Stat Read ==="));

                                    MinecraftClient client = MinecraftClient.getInstance();
                                    if (client.player == null) {
                                        context.getSource().sendFeedback(
                                                Text.literal("§cPlayer is null!"));
                                        return 0;
                                    }

                                    StatHandler statHandler = client.player.getStatHandler();
                                    Identifier stoneId = Identifier.ofVanilla("stone");

                                    // Force read the stat to trigger mixin
                                    GSC.LOGGER.info("=== FORCING STAT READ ===");
                                    int statValue = statHandler.getStat(Stats.MINED.getOrCreateStat(
                                            net.minecraft.registry.Registries.BLOCK.get(stoneId)));

                                    GSC.LOGGER.info("Stat read returned: {}", statValue);
                                    context.getSource().sendFeedback(
                                            Text.literal("§7Stat value: §b" + statValue));
                                    context.getSource().sendFeedback(
                                            Text.literal("§7Check console! If mixin works, you'll see 'StatHandlerMixin is working!'"));

                                    return 1;
                                }))
        );
    }
}