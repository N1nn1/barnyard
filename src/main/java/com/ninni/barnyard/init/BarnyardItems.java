package com.ninni.barnyard.init;

import com.ninni.barnyard.Barnyard;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

@SuppressWarnings("unused")
public class BarnyardItems {

    public static final Item THATCH_BLOCK = register("thatch_block", new BlockItem(BarnyardBlocks.THATCH_BLOCK, new FabricItemSettings().group(CreativeModeTab.TAB_BUILDING_BLOCKS)));
    public static final Item THATCH = register("thatch", new BlockItem(BarnyardBlocks.THATCH, new FabricItemSettings().group(CreativeModeTab.TAB_BUILDING_BLOCKS)));


    private static Item register(String id, Item item) {
        return Registry.register(Registry.ITEM, new ResourceLocation(Barnyard.MOD_ID, id), item);
    }
}
