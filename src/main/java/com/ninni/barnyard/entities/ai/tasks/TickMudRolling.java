package com.ninni.barnyard.entities.ai.tasks;

import com.google.common.collect.ImmutableMap;
import com.ninni.barnyard.entities.BarnyardPig;
import com.ninni.barnyard.init.BarnyardMemoryModules;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class TickMudRolling extends Behavior<BarnyardPig> {

    public TickMudRolling() {
        super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED, BarnyardMemoryModules.NEAREST_MUD, MemoryStatus.VALUE_PRESENT, BarnyardMemoryModules.MUD_ROLLING_COOLDOWN_TICKS, MemoryStatus.VALUE_ABSENT, BarnyardMemoryModules.MUD_ROLLING_TICKS, MemoryStatus.VALUE_PRESENT), 120);
    }

    @Override
    protected boolean canStillUse(ServerLevel serverLevel, BarnyardPig livingEntity, long l) {
        return true;
    }

    @Override
    protected void start(ServerLevel serverLevel, BarnyardPig livingEntity, long l) {
        livingEntity.setPose(Pose.DIGGING);
    }

    @Override
    protected void stop(ServerLevel serverLevel, BarnyardPig livingEntity, long l) {
        livingEntity.setPose(Pose.STANDING);
        livingEntity.getBrain().setMemory(BarnyardMemoryModules.MUD_ROLLING_COOLDOWN_TICKS, 6000);
        livingEntity.getBrain().eraseMemory(BarnyardMemoryModules.NEAREST_MUD);
        livingEntity.getBrain().eraseMemory(BarnyardMemoryModules.MUD_ROLLING_TICKS);
    }

}
