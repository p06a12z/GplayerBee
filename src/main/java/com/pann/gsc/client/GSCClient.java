package com.pann.gsc.client;

import com.pann.gsc.GSC;
import com.pann.gsc.command.AutoAttackCommand;
import com.pann.gsc.command.DebugCommand;
import com.pann.gsc.command.FakeStatCommand;
import com.pann.gsc.command.FriendsCommand;
import com.pann.gsc.command.GlowCommand;
import com.pann.gsc.command.IgnoreFoliageCommand;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class GSCClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        GSC.LOGGER.info("Glow Stats Client - Client side initialized!");

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            GlowCommand.register(dispatcher);
            FriendsCommand.register(dispatcher);  // THÊM DÒNG NÀY
            FakeStatCommand.register(dispatcher, registryAccess);
            DebugCommand.register(dispatcher);
            AutoAttackCommand.register(dispatcher);
            IgnoreFoliageCommand.register(dispatcher);
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            AutoAttackerManager.tick();
        });
    }
}