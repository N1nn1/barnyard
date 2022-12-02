package com.ninni.barnyard.entities.ai.tasks;

import com.google.common.collect.ImmutableMap;
import com.ninni.barnyard.init.BarnyardPose;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

import java.util.Optional;

public class Rest<T extends LivingEntity> extends Behavior<T> {

    public Rest() {
        super(ImmutableMap.of(MemoryModuleType.TEMPTING_PLAYER, MemoryStatus.VALUE_ABSENT, MemoryModuleType.IS_PANICKING, MemoryStatus.VALUE_ABSENT));
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel serverLevel, T livingEntity) {
        return !livingEntity.getBrain().hasMemoryValue(MemoryModuleType.HURT_BY_ENTITY) && serverLevel.isNight() && livingEntity.isOnGround() && !livingEntity.isInWater() && !livingEntity.hasPose(BarnyardPose.RESTING.get()) && this.findRestPosition(serverLevel, livingEntity).isPresent() && !livingEntity.isVehicle();
    }

    @Override
    protected boolean canStillUse(ServerLevel serverLevel, T livingEntity, long l) {
        return this.checkExtraStartConditions(serverLevel, livingEntity);
    }

    @Override
    protected void start(ServerLevel serverLevel, T livingEntity, long l) {
        this.findRestPosition(serverLevel, livingEntity).ifPresent(blockPos -> {
            BehaviorUtils.setWalkAndLookTargetMemories(livingEntity, blockPos, 1.0F, 0);
            if (blockPos.distManhattan(livingEntity.blockPosition()) <= 1.0D) {
                livingEntity.setPose(BarnyardPose.RESTING.get());
            }
        });
    }

    private Optional<BlockPos> findRestPosition(ServerLevel level, LivingEntity mob) {
        BlockPos blockPos = new BlockPos(mob.getX(), mob.getBoundingBox().maxY, mob.getZ());
        //Still need to fix the findClosestMatch method since it doesn't use the maxY of the entity's bounding box to check the skylight level
        return Optional.of(blockPos).filter(blockPos1 -> cannotSeeSky(level, blockPos1)).or(() -> BlockPos.findClosestMatch(blockPos, 16, 16, blockPos1 -> cannotSeeSky(level, blockPos1)));
    }

    private boolean cannotSeeSky(ServerLevel level, BlockPos blockPos1) {
        return !level.canSeeSky(blockPos1);
    }

}
