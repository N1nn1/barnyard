package com.ninni.barnyard.entities.ai.tasks;

import com.google.common.collect.ImmutableMap;
import com.ninni.barnyard.init.BarnyardPose;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class QuitResting<T extends LivingEntity> extends Behavior<T> {

    public QuitResting() {
        super(ImmutableMap.of());
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel serverLevel, T livingEntity) {
        return livingEntity.getBrain().hasMemoryValue(MemoryModuleType.HURT_BY_ENTITY) || (serverLevel.isDay() && livingEntity.hasPose(BarnyardPose.RESTING.get()));
    }

    @Override
    protected void start(ServerLevel serverLevel, T livingEntity, long l) {
        livingEntity.setPose(Pose.STANDING);
    }

}
