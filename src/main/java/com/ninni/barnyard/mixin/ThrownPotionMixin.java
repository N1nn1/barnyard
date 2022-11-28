package com.ninni.barnyard.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.ninni.barnyard.entities.BarnyardPig;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

@Mixin(ThrownPotion.class)
public abstract class ThrownPotionMixin extends ThrowableItemProjectile {
    public ThrownPotionMixin(EntityType<? extends ThrowableItemProjectile> type, LivingEntity mob, Level level) {
        super(type, mob, level);
    }

    @Inject(at = @At("TAIL"), method = "applyWater")
    private void applyWater(CallbackInfo info) {
        AABB aABB = getBoundingBox().inflate(4, 2, 4);
        level.getEntitiesOfClass(BarnyardPig.class, aABB).forEach((mob) -> {
            mob.setMuddy(false);
        });
    }
}