package com.ninni.barnyard.init;

import static com.ninni.barnyard.Barnyard.BARNYARD_TAB;
import static com.ninni.barnyard.Barnyard.MOD_ID;

import com.ninni.barnyard.items.TruffleStewItem;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;

public class BarnyardItems {

    public static final Item THATCH_BLOCK = register("thatch_block", new BlockItem(BarnyardBlocks.THATCH_BLOCK, new FabricItemSettings().group(BARNYARD_TAB)));
    public static final Item THATCH = register("thatch", new BlockItem(BarnyardBlocks.THATCH, new FabricItemSettings().group(BARNYARD_TAB)));

    public static final Item BARNYARD_PIG_SPAWN_EGG = register("barnyard_pig_spawn_egg", new SpawnEggItem(BarnyardEntityTypes.PIG, 15771042, 14377823, new FabricItemSettings().maxCount(64).group(BARNYARD_TAB)));
    public static final Item BARNYARD_RABBIT_SPAWN_EGG = register("barnyard_rabbit_spawn_egg", new SpawnEggItem(BarnyardEntityTypes.RABBIT, 10051392, 7555121, new FabricItemSettings().maxCount(64).group(BARNYARD_TAB)));

    public static final Item TRUFFLE = register("truffle", new Item(new FabricItemSettings().group(BARNYARD_TAB)));
    public static final Item TRUFFLE_STEW = register("truffle_stew", new TruffleStewItem(new FabricItemSettings().group(BARNYARD_TAB).stacksTo(1).food(BarnyardFoods.TRUFFLE_STEW)));

    private static Item register(String id, Item item) {
        return Registry.register(Registry.ITEM, new ResourceLocation(MOD_ID, id), item);
    }
}