package com.ninni.barnyard.entities.ai.tasks;

import com.google.common.collect.ImmutableMap;
import com.ninni.barnyard.entities.BarnyardPig;
import com.ninni.barnyard.init.BarnyardMemoryModules;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class TickMudRolling extends Behavior<BarnyardPig> {

    public TickMudRolling() {
        super(ImmutableMap.of(BarnyardMemoryModules.MUD_ROLLING_TICKS, MemoryStatus.VALUE_PRESENT, BarnyardMemoryModules.MUD_ROLLING_COOLDOWN_TICKS, MemoryStatus.VALUE_ABSENT), 120);
    }

    @Override
    protected boolean canStillUse(ServerLevel level, BarnyardPig mob, long l) {
        return true;
    }

    @Override
    protected void tick(ServerLevel level, BarnyardPig mob, long l) {
        var memory = mob.getBrain().getMemory(BarnyardMemoryModules.MUD_ROLLING_TICKS);
        if (memory.isPresent()) {
            int time = memory.get();
            if (time == 50) mob.setMuddy(true);
        }
    }

    @Override
    protected void stop(ServerLevel level, BarnyardPig mob, long l) {
        mob.setPose(Pose.STANDING);
        mob.getBrain().setMemory(BarnyardMemoryModules.MUD_ROLLING_COOLDOWN_TICKS, 6000);
        mob.getBrain().eraseMemory(BarnyardMemoryModules.NEAREST_MUD);
        mob.getBrain().eraseMemory(BarnyardMemoryModules.MUD_ROLLING_TICKS);
    }
}