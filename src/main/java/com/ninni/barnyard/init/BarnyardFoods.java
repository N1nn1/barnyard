package com.ninni.barnyard.init;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;

public interface BarnyardFoods {
    FoodProperties TRUFFLE_STEW = (new FoodProperties.Builder())
            .nutrition(6)
            .saturationMod(0.6F)
            .effect(new MobEffectInstance(MobEffects.DIG_SPEED, 20 * 60 * 4), 1.0F)
            .build();
}
