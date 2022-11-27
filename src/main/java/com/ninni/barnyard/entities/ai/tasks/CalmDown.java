package com.ninni.barnyard.entities.ai.tasks;

import com.google.common.collect.ImmutableMap;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class CalmDown extends Behavior<LivingEntity> {

    public CalmDown() {
        super(ImmutableMap.of(MemoryModuleType.HURT_BY_ENTITY, MemoryStatus.VALUE_PRESENT));
    }

    @Override
    protected void start(ServerLevel level, LivingEntity mob, long l) {
        if (!isNearAttacker(mob)) {
            mob.getBrain().eraseMemory(MemoryModuleType.HURT_BY);
            mob.getBrain().eraseMemory(MemoryModuleType.HURT_BY_ENTITY);
        }
    }

    private static boolean isNearAttacker(LivingEntity mob) {
        return mob.getBrain().getMemory(MemoryModuleType.HURT_BY_ENTITY).filter(next -> mob.distanceToSqr(next) <= 48).isPresent();
    }
}