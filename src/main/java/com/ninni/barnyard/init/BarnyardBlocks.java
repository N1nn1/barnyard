package com.ninni.barnyard.init;

import com.ninni.barnyard.Barnyard;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CarpetBlock;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

public class BarnyardBlocks {

    public static final Block THATCH_BLOCK = register("thatch_block", new Block(FabricBlockSettings.of(Material.GRASS, MaterialColor.COLOR_YELLOW).strength(0.25F).sound(BarnyardSounds.THATCH)));
    public static final Block THATCH = register("thatch", new CarpetBlock(FabricBlockSettings.copyOf(THATCH_BLOCK).strength(0.1F)));

    private static Block register(String id, Block block) {
        return Registry.register(Registry.BLOCK, new ResourceLocation(Barnyard.MOD_ID, id), block);
    }
}
