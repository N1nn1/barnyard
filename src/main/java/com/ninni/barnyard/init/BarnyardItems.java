package com.ninni.barnyard.init;

import com.ninni.barnyard.Barnyard;
import com.ninni.barnyard.items.TruffleStewItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;

@SuppressWarnings("unused")
public class BarnyardItems {

    public static final Item THATCH_BLOCK = register("thatch_block", new BlockItem(BarnyardBlocks.THATCH_BLOCK, new FabricItemSettings().group(CreativeModeTab.TAB_BUILDING_BLOCKS)));
    public static final Item THATCH = register("thatch", new BlockItem(BarnyardBlocks.THATCH, new FabricItemSettings().group(CreativeModeTab.TAB_BUILDING_BLOCKS)));

    public static final Item TRUFFLE = register("truffle", new Item(new FabricItemSettings().group(CreativeModeTab.TAB_FOOD).rarity(Rarity.UNCOMMON)));
    public static final Item TRUFFLE_STEW = register("truffle_stew", new TruffleStewItem(new FabricItemSettings().group(CreativeModeTab.TAB_FOOD).stacksTo(1).rarity(Rarity.UNCOMMON).food(BarnyardFoods.TRUFFLE_STEW)));


    private static Item register(String id, Item item) {
        return Registry.register(Registry.ITEM, new ResourceLocation(Barnyard.MOD_ID, id), item);
    }
}
