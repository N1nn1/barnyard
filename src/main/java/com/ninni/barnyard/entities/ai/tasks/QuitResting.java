package com.ninni.barnyard.entities.ai.tasks;

import com.google.common.collect.ImmutableMap;
import com.ninni.barnyard.init.BarnyardMemoryModules;
import com.ninni.barnyard.init.BarnyardPose;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

import java.util.Optional;

public class QuitResting<T extends LivingEntity> extends Behavior<T> {

    public QuitResting() {
        super(ImmutableMap.of());
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel serverLevel, T livingEntity) {
        Optional<BlockPos> memory = livingEntity.getBrain().getMemory(BarnyardMemoryModules.REST_SPOT);
        if (serverLevel.isDay() && livingEntity.hasPose(BarnyardPose.RESTING.get())) {
            return true;
        }
        if (memory.isPresent()) {
            boolean flag = !serverLevel.getBlockState(memory.get()).canOcclude() && serverLevel.canSeeSky(memory.get());
            if (livingEntity.isOnGround()) {
                return false;
            } else if (flag) {
                return true;
            } else if (serverLevel.getBlockState(memory.get()).canOcclude() && serverLevel.canSeeSky(memory.get())) {
                return true;
            }
        }
        return livingEntity.getBrain().hasMemoryValue(MemoryModuleType.HURT_BY_ENTITY);
    }

    @Override
    protected void start(ServerLevel serverLevel, T livingEntity, long l) {
        livingEntity.setPose(Pose.STANDING);
        livingEntity.getBrain().eraseMemory(BarnyardMemoryModules.REST_SPOT);
    }

}
