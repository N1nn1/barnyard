package com.ninni.barnyard;

import com.google.common.reflect.Reflection;
import com.ninni.barnyard.init.BarnyardBlocks;
import com.ninni.barnyard.init.BarnyardItems;
import net.fabricmc.api.ModInitializer;

public class Barnyard implements ModInitializer {
	public static final String MOD_ID = "barnyard";

	@Override
	@SuppressWarnings("UnstableApiUsage")
	public void onInitialize() {
		Reflection.initialize(
				BarnyardItems.class,
				BarnyardBlocks.class
		);
	}
}
