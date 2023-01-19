package com.ninni.barnyard.entities.ai.tasks;

import com.google.common.collect.ImmutableMap;
import com.ninni.barnyard.entities.BarnyardPig;
import com.ninni.barnyard.init.BarnyardMemoryModules;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.phys.AABB;

import java.util.Optional;

public class GoToRestSpot extends Behavior<BarnyardPig> {

    public GoToRestSpot() {
        super(ImmutableMap.of(BarnyardMemoryModules.REST_SPOT, MemoryStatus.VALUE_PRESENT, MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED));
    }

    @Override
    protected boolean canStillUse(ServerLevel serverLevel, BarnyardPig livingEntity, long l) {
        return true;
    }

    @Override
    protected boolean timedOut(long l) {
        return false;
    }

    @Override
    protected void start(ServerLevel serverLevel, BarnyardPig livingEntity, long l) {
        this.getDestination(livingEntity).filter(blockPos -> this.isValid(livingEntity, blockPos)).ifPresent(blockPos -> BehaviorUtils.setWalkAndLookTargetMemories(livingEntity, blockPos, 1.0F, 0));
    }

    private boolean isValid(BarnyardPig livingEntity, BlockPos blockPos) {
        if (!livingEntity.level.noCollision(livingEntity, new AABB(blockPos))) {
            return false;
        }
        return !this.isCloseToRestSpot(livingEntity, blockPos) && livingEntity.level.canSeeSky(blockPos);
    }

    private Optional<BlockPos> getDestination(BarnyardPig livingEntity) {
        return livingEntity.getBrain().getMemory(BarnyardMemoryModules.REST_SPOT);
    }

    private boolean isCloseToRestSpot(BarnyardPig livingEntity, BlockPos blockPos) {
        return blockPos.distManhattan(livingEntity.blockPosition()) <= 0;
    }
}
