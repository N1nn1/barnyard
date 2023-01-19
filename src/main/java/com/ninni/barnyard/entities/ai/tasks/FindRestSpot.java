package com.ninni.barnyard.entities.ai.tasks;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.ninni.barnyard.init.BarnyardBlocks;
import com.ninni.barnyard.init.BarnyardMemoryModules;
import com.ninni.barnyard.init.BarnyardPose;
import net.fabricmc.fabric.api.item.v1.CustomDamageHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Optional;

public class FindRestSpot<T extends LivingEntity> extends Behavior<T> {
    private BlockPos blockPos;

    public FindRestSpot() {
        super(ImmutableMap.of(BarnyardMemoryModules.REST_SPOT, MemoryStatus.VALUE_ABSENT, MemoryModuleType.TEMPTING_PLAYER, MemoryStatus.VALUE_ABSENT, MemoryModuleType.IS_PANICKING, MemoryStatus.VALUE_ABSENT));
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel serverLevel, T livingEntity) {
        if (livingEntity.getBrain().hasMemoryValue(MemoryModuleType.HURT_BY_ENTITY) || livingEntity.isVehicle() || livingEntity.isInWater() || serverLevel.isDay()) {
            return false;
        }
        Optional<BlockPos> restPosition = this.findRestPosition(serverLevel, livingEntity);
        if (restPosition.isPresent()) {
            this.blockPos = restPosition.get();
            return livingEntity.isOnGround() && !livingEntity.hasPose(BarnyardPose.RESTING.get()) && !livingEntity.isVehicle();
        }
        return this.blockPos != null;
    }

    @Override
    protected void start(ServerLevel serverLevel, T livingEntity, long l) {
        if (this.blockPos != null) {
            livingEntity.getBrain().setMemory(BarnyardMemoryModules.REST_SPOT, this.blockPos);
        }
    }

    private Optional<BlockPos> findRestPosition(ServerLevel world, LivingEntity mob) {
        List<BlockPos> preferrables = Lists.newArrayList();
        List<BlockPos> nonPreferables = Lists.newArrayList();
        int range = 8;
        for (int x = -range; x <= range; x++) {
            for (int z = -range; z <= range; z++) {
                for (int y = -range; y <= range; y++) {
                    BlockPos blockPos = new BlockPos(mob.getX() + x, mob.getY() + y, mob.getZ() + z);
                    BlockState blockState = world.getBlockState(blockPos);
                    if ((blockState.is(BarnyardBlocks.THATCH_BLOCK) || blockState.is(BarnyardBlocks.THATCH)) && world.getBlockState(blockPos.above()).isAir()) {
                        preferrables.add(blockPos);
                    }
                    BlockPos offset = new BlockPos(mob.getX() + x, mob.getBoundingBox().maxY, mob.getZ() + z);
                    if (!world.canSeeSky(offset)) {
                        nonPreferables.add(offset);
                    }
                }
            }
        }
        if (!preferrables.isEmpty()) {
            return Optional.of(preferrables.get(world.getRandom().nextInt(preferrables.size())));
        } else if (!nonPreferables.isEmpty()) {
            return Optional.of(nonPreferables.get(world.getRandom().nextInt(nonPreferables.size())));
        }
        return Optional.empty();
    }

}
