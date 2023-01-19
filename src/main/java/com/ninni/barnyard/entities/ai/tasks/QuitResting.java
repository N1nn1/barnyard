package com.ninni.barnyard.entities.ai.tasks;

import com.google.common.collect.ImmutableMap;
import com.ninni.barnyard.entities.AbstractHappyAnimal;
import com.ninni.barnyard.init.BarnyardBlocks;
import com.ninni.barnyard.init.BarnyardMemoryModules;
import com.ninni.barnyard.init.BarnyardPose;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public class QuitResting<T extends AbstractHappyAnimal> extends Behavior<T> {

    public QuitResting() {
        super(ImmutableMap.of());
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel serverLevel, T livingEntity) {
        Optional<BlockPos> memory = livingEntity.getBrain().getMemory(BarnyardMemoryModules.REST_SPOT);
        if (livingEntity.getBrain().hasMemoryValue(MemoryModuleType.HURT_BY_ENTITY) || (serverLevel.isDay() && livingEntity.hasPose(BarnyardPose.RESTING.get()))) {
            return true;
        } else if (memory.isPresent()) {
            if (livingEntity.isOnGround()) {
                return false;
            } else return serverLevel.canSeeSky(memory.get());
        }
        return false;
    }

    @Override
    protected void start(ServerLevel serverLevel, T livingEntity, long l) {
        livingEntity.setPose(Pose.STANDING);
        livingEntity.getBrain().eraseMemory(BarnyardMemoryModules.REST_SPOT);
        BlockState state = livingEntity.level.getBlockState(livingEntity.blockPosition().below());
        if (state.is(BarnyardBlocks.THATCH_BLOCK) || state.is(BarnyardBlocks.THATCH)) {
            livingEntity.increaseHappyLevel();
        }
    }

    @Override
    protected void stop(ServerLevel serverLevel, T livingEntity, long l) {
    }

}
