package com.ninni.barnyard.entities.ai.tasks;

import java.util.Optional;

import com.google.common.collect.ImmutableMap;
import com.ninni.barnyard.init.BarnyardMemoryModules;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.phys.Vec3;

public class FindPlaceToRestAndSleep extends Behavior<LivingEntity> {
    public FindPlaceToRestAndSleep() {
        super(ImmutableMap.of(BarnyardMemoryModules.IS_SLEEPING, MemoryStatus.VALUE_ABSENT));
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, LivingEntity mob) {
        return !mob.hasPose(Pose.ROARING) && level.isNight() && getRestPosition(level, mob).isPresent();
    }

    protected Optional<BlockPos> getRestPosition(ServerLevel level, LivingEntity mob) {
        return BlockPos.findClosestMatch(mob.blockPosition(), 16, 16, (pos) -> !level.canSeeSky(pos));
    }

    @Override
    protected void start(ServerLevel level, LivingEntity mob, long l) {
        getRestPosition(level, mob).ifPresent((rest) -> {
            BehaviorUtils.setWalkAndLookTargetMemories(mob, rest, 1, 0);
            int distance = rest.distManhattan(mob.blockPosition());
            if (distance <= 1) {
                mob.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
                mob.getBrain().setMemory(BarnyardMemoryModules.IS_SLEEPING, Unit.INSTANCE);
                mob.setPose(Pose.ROARING);

                mob.setDeltaMovement(Vec3.ZERO);
                mob.hasImpulse = true;
            }
        });
    }
}