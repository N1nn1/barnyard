package com.ninni.barnyard.entities.ai.tasks;

import java.util.Optional;

import com.google.common.collect.ImmutableMap;
import com.ninni.barnyard.entities.BarnyardPig;
import com.ninni.barnyard.init.BarnyardMemoryModules;
import com.ninni.barnyard.init.BarnyardSounds;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class MudRolling extends Behavior<BarnyardPig> {

    public MudRolling() {
        super(ImmutableMap.of(BarnyardMemoryModules.MUD_ROLLING_COOLDOWN_TICKS, MemoryStatus.VALUE_ABSENT, BarnyardMemoryModules.NEAREST_MUD, MemoryStatus.VALUE_PRESENT, BarnyardMemoryModules.MUD_ROLLING_TICKS, MemoryStatus.VALUE_ABSENT, MemoryModuleType.IS_SNIFFING, MemoryStatus.VALUE_ABSENT, MemoryModuleType.BREED_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.IS_PANICKING, MemoryStatus.VALUE_ABSENT));
    }

    protected boolean canStillUse(ServerLevel level, BarnyardPig mob, long l) {
        return mob.getBrain().hasMemoryValue(BarnyardMemoryModules.MUD_ROLLING_COOLDOWN_TICKS);
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, BarnyardPig mob) {
        return getNearestCluster(mob).isPresent();
    }

    @Override
    protected void start(ServerLevel level, BarnyardPig mob, long l) {
        getNearestCluster(mob).ifPresent(pos -> {
            BehaviorUtils.setWalkAndLookTargetMemories(mob, pos, 1, 0);
            int distance = mob.blockPosition().distManhattan(pos);
            if (distance <= 1) {
                mob.setPose(Pose.DIGGING);
                mob.playSound(BarnyardSounds.PIG_MUD_ROLL, 1, 1);
                mob.getBrain().setMemory(BarnyardMemoryModules.MUD_ROLLING_TICKS, 100);
            }
        });
    }

    private Optional<BlockPos> getNearestCluster(BarnyardPig mob) {
        return mob.getBrain().getMemory(BarnyardMemoryModules.NEAREST_MUD);
    }
}