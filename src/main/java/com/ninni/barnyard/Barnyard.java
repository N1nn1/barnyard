package com.ninni.barnyard;


import com.ninni.barnyard.init.*;
import net.fabricmc.fabric.api.biome.v1.BiomeModification;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.biome.MobSpawnSettings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.reflect.Reflection;
import com.ninni.barnyard.entities.BarnyardPig;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class Barnyard implements ModInitializer {
	public static final String MOD_ID = "barnyard";
	public static final Logger LOGGER = LogManager.getLogger();
	public static final CreativeModeTab BARNYARD_TAB = FabricItemGroupBuilder.create(new ResourceLocation(MOD_ID, MOD_ID)).icon(() -> new ItemStack(BarnyardItems.TRUFFLE)).build();

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
				BarnyardParticleTypes.class,
				BarnyardEntityTypes.class,
				BarnyardSensorTypes.class,
				BarnyardActivities.class
		);

		this.replaceEntity(EntityType.PIG, BarnyardEntityTypes.PIG, 10, 4, 4);

		UseItemCallback.EVENT.register((player, world, hand) -> {
			ItemStack stack = player.getItemInHand(hand);
			Item item = stack.getItem();
			if (stack.is(Items.CARROT_ON_A_STICK) && !player.getCooldowns().isOnCooldown(item) && player.getVehicle() instanceof BarnyardPig pig) {
				pig.boost();
				pig.playSound(pig.getEatingSound(stack));
				stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
				player.getCooldowns().addCooldown(item, 20);
				if (stack.isEmpty()) {
					ItemStack fishingRod = new ItemStack(Items.FISHING_ROD);
					fishingRod.setTag(stack.getTag());
					return InteractionResultHolder.success(fishingRod);
				}
				player.awardStat(Stats.ITEM_USED.get(item));
				return InteractionResultHolder.success(stack);
			}
			return InteractionResultHolder.pass(stack);
		});
	}

	private void replaceEntity(EntityType<?> from, EntityType<?> to, int weight, int minGroupSize, int maxGroupSize) {
		BiomeModifications.addSpawn(context -> BiomeSelectors.spawnsOneOf(from).test(context), to.getCategory(), to, weight, minGroupSize, maxGroupSize);
		BiomeModifications.create(new ResourceLocation(MOD_ID, "_replace_" + Registry.ENTITY_TYPE.getKey(from).getPath())).add(ModificationPhase.REPLACEMENTS,
			context -> BiomeSelectors.spawnsOneOf(from).test(context),
			(selector, modifier) -> modifier.getSpawnSettings().removeSpawnsOfEntityType(from)
		);
	}
}