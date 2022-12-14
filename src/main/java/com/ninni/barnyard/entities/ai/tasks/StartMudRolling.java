package com.ninni.barnyard.entities.ai.tasks;

import java.util.Optional;

import com.google.common.collect.ImmutableMap;
import com.ninni.barnyard.entities.BarnyardPig;
import com.ninni.barnyard.entities.ai.BarnyardPigAi;
import com.ninni.barnyard.init.BarnyardMemoryModules;
import com.ninni.barnyard.init.BarnyardSounds;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class StartMudRolling extends Behavior<BarnyardPig> {

    public StartMudRolling() {
        super(ImmutableMap.of(BarnyardMemoryModules.MUD_COOLDOWN, MemoryStatus.VALUE_ABSENT, BarnyardMemoryModules.NEAREST_MUD, MemoryStatus.VALUE_PRESENT));
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, BarnyardPig mob) {
        return getNearestCluster(mob).isPresent();
    }

    @Override
    protected void start(ServerLevel level, BarnyardPig mob, long l) {
        getNearestCluster(mob).ifPresent(pos -> {
            BehaviorUtils.setWalkAndLookTargetMemories(mob, pos, 1, 0);
            int distance = pos.distManhattan(mob.blockPosition());
            if (distance <= 1) {
                Brain<BarnyardPig> brain = mob.getBrain();

                brain.setMemory(BarnyardMemoryModules.IS_ROLLING_IN_MUD, Unit.INSTANCE);
                brain.setMemory(BarnyardMemoryModules.MUD_ROLLING_TICKS, BarnyardPigAi.SNIFFING_DURATION);
                brain.eraseMemory(MemoryModuleType.WALK_TARGET);

                mob.setPose(Pose.DIGGING);
                mob.playSound(BarnyardSounds.PIG_MUD_ROLL, 1, 1);
            }
        });
    }

    private Optional<BlockPos> getNearestCluster(BarnyardPig mob) {
        return mob.getBrain().getMemory(BarnyardMemoryModules.NEAREST_MUD);
    }
}