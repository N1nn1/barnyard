package com.ninni.barnyard;


import com.ninni.barnyard.init.BarnyardActivities;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.reflect.Reflection;
import com.ninni.barnyard.init.BarnyardBlocks;
import com.ninni.barnyard.init.BarnyardEntityTypes;
import com.ninni.barnyard.init.BarnyardItems;
import com.ninni.barnyard.init.BarnyardSensorTypes;
import com.ninni.barnyard.init.BarnyardSounds;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ItemSteerable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class Barnyard implements ModInitializer {

	public static final String MOD_ID = "barnyard";
	public static final Logger LOGGER = LogManager.getLogger();

	public static ResourceLocation id(String name) {
		return new ResourceLocation(MOD_ID, name);
	}

	@Override
	@SuppressWarnings("UnstableApiUsage")
	public void onInitialize() {
		Reflection.initialize(
				BarnyardSounds.class,
				BarnyardItems.class,
				BarnyardBlocks.class,
				BarnyardEntityTypes.class,
				BarnyardSensorTypes.class,
				BarnyardActivities.class
		);
		UseItemCallback.EVENT.register((player, world, hand) -> {
			ItemStack stack = player.getItemInHand(hand);
			Entity vehicle = player.getVehicle();
			Item item = stack.getItem();
			if (!player.getCooldowns().isOnCooldown(item) && player.isPassenger() && stack.is(Items.CARROT_ON_A_STICK) && vehicle instanceof ItemSteerable itemSteerable && vehicle.getType() == BarnyardEntityTypes.PIG) {
				itemSteerable.boost();
				stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
				player.getCooldowns().addCooldown(item, 20);
				if (stack.isEmpty()) {
					ItemStack itemStack2 = new ItemStack(Items.FISHING_ROD);
					itemStack2.setTag(stack.getTag());
					return InteractionResultHolder.success(itemStack2);
				}
				player.awardStat(Stats.ITEM_USED.get(item));
				return InteractionResultHolder.success(stack);
			}
			return InteractionResultHolder.pass(stack);
		});
	}
}