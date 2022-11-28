package com.ninni.barnyard.mixin;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.ninni.barnyard.entities.BarnyardPig;

import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(LayeredCauldronBlock.class)
public abstract class LayeredCauldronBlockMixin extends AbstractCauldronBlock {
    public LayeredCauldronBlockMixin(Properties properties, Map<Item, CauldronInteraction> map) {
        super(properties, map);
    }

    @Inject(at = @At("TAIL"), method = "entityInside")
    public void B$entityInside(BlockState state, Level level, BlockPos pos, Entity entity, CallbackInfo info) {
        if (!level.isClientSide() && entity instanceof BarnyardPig mob && mob.isMuddy() && isEntityInsideContent(state, pos, mob)) {
            mob.setMuddy(false);
        }
    }
}