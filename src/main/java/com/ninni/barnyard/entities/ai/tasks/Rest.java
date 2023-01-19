package com.ninni.barnyard.entities.ai.tasks;

import com.google.common.collect.ImmutableMap;
import com.ninni.barnyard.entities.BarnyardPig;
import com.ninni.barnyard.init.BarnyardMemoryModules;
import com.ninni.barnyard.init.BarnyardPose;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.phys.AABB;

import java.util.Optional;

public class Rest extends Behavior<BarnyardPig> {

    public Rest() {
        super(ImmutableMap.of());
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel serverLevel, BarnyardPig livingEntity) {
        return !livingEntity.hasPose(BarnyardPose.RESTING.get()) && serverLevel.isNight();
    }

    @Override
    protected boolean canStillUse(ServerLevel serverLevel, BarnyardPig livingEntity, long l) {
        if (livingEntity.getBrain().getMemory(BarnyardMemoryModules.REST_SPOT).isPresent() && serverLevel.getEntitiesOfClass(Entity.class, new AABB(livingEntity.getBrain().getMemory(BarnyardMemoryModules.REST_SPOT).get().above())).size() >= 1) {
            return false;
        }
        Optional<BlockPos> memory = livingEntity.getBrain().getMemory(BarnyardMemoryModules.REST_SPOT);
        return memory.map(blockPos -> !this.isCloseEnough(livingEntity, blockPos)).orElse(true) && serverLevel.isNight();
    }

    private boolean isCloseEnough(LivingEntity livingEntity, BlockPos blockPos) {
        return blockPos.distManhattan(livingEntity.blockPosition()) <= 1;
    }

    @Override
    protected void start(ServerLevel serverLevel, BarnyardPig livingEntity, long l) {
        livingEntity.getBrain().getMemory(BarnyardMemoryModules.REST_SPOT).ifPresent(blockPos -> BehaviorUtils.setWalkAndLookTargetMemories(livingEntity, blockPos, 1.0F, 0));
    }

    @Override
    protected void stop(ServerLevel serverLevel, BarnyardPig livingEntity, long l) {
        livingEntity.getBrain().getMemory(BarnyardMemoryModules.REST_SPOT).filter(blockPos -> this.isCloseEnough(livingEntity, blockPos)).ifPresent(blockPos -> {
            livingEntity.setPose(BarnyardPose.RESTING.get());
        });
    }
}
