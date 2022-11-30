package com.ninni.barnyard.entities.ai.tasks;

import com.google.common.collect.ImmutableMap;
import com.ninni.barnyard.init.BarnyardMemoryModules;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class AnimalWakeUp extends Behavior<LivingEntity> {
    public AnimalWakeUp() {
        super(ImmutableMap.of(BarnyardMemoryModules.IS_SLEEPING, MemoryStatus.VALUE_PRESENT));
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, LivingEntity mob) {
        return level.isDay() && mob.hasPose(Pose.ROARING);
    }
    
    @Override
    protected void start(ServerLevel level, LivingEntity mob, long l) {
        mob.getBrain().eraseMemory(BarnyardMemoryModules.IS_SLEEPING);
        mob.setPose(Pose.STANDING);
    }
}