package com.ninni.barnyard.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.BowlFoodItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TruffleStewItem extends BowlFoodItem {
    public TruffleStewItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        MutableComponent effectDuration = Component.translatable("tooltip.truffle_stew.effect_duration");
        list.add(effectDuration.withStyle(ChatFormatting.BLUE));
        MutableComponent indent = Component.translatable("");
        list.add(indent);
        MutableComponent consumableText = Component.translatable("tooltip.food_item");
        list.add(consumableText.withStyle(ChatFormatting.DARK_PURPLE));
        MutableComponent effectAttack = Component.translatable("tooltip.truffle_stew.effect.attack");
        list.add(effectAttack.withStyle(ChatFormatting.BLUE));
        MutableComponent effectMine = Component.translatable("tooltip.truffle_stew.effect.mine");
        list.add(effectMine.withStyle(ChatFormatting.BLUE));
        super.appendHoverText(itemStack, level, list, tooltipFlag);
    }
}
