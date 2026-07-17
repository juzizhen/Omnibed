package com.juzizhen.omnibed;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Omnibed implements ModInitializer {
	public static final String MOD_ID = "omnibed";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Omnibed is Initialize!");
	}
}
