package com.pann.gsc;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GSC implements ModInitializer {
    public static final String MOD_ID = "gsc";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Glow Stats Client initialized!");
    }
}
