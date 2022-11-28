package com.ninni.barnyard.entities.ai.tasks;

import com.google.common.collect.ImmutableMap;
import com.ninni.barnyard.entities.BarnyardPig;
import com.ninni.barnyard.entities.ai.BarnyardPigAi;
import com.ninni.barnyard.init.BarnyardMemoryModules;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class TickMudRolling extends Behavior<BarnyardPig> {

    public TickMudRolling(int duration) {
        super(ImmutableMap.of(BarnyardMemoryModules.IS_ROLLING_IN_MUD, MemoryStatus.VALUE_PRESENT, BarnyardMemoryModules.MUD_COOLDOWN,
                MemoryStatus.VALUE_ABSENT, BarnyardMemoryModules.MUD_ROLLING_TICKS, MemoryStatus.VALUE_PRESENT,
                MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT), duration);
    }

    @Override
    protected boolean canStillUse(ServerLevel level, BarnyardPig mob, long l) {
        return true;
    }

    @Override
    protected void tick(ServerLevel level, BarnyardPig mob, long l) {
        mob.getBrain().getMemory(BarnyardMemoryModules.MUD_ROLLING_TICKS).ifPresent((time) -> {
            if (time == 50) mob.setMuddy(true);
        });
    }

    @Override
    protected void stop(ServerLevel level, BarnyardPig mob, long l) {
        mob.setPose(Pose.STANDING);
        mob.getBrain().eraseMemory(BarnyardMemoryModules.NEAREST_MUD);
        mob.getBrain().eraseMemory(BarnyardMemoryModules.IS_ROLLING_IN_MUD);
        mob.getBrain().setMemoryWithExpiry(BarnyardMemoryModules.MUD_COOLDOWN, Unit.INSTANCE, BarnyardPigAi.MUD_ROLLING_COOLDOWN.sample(mob.getRandom()));
    }
}