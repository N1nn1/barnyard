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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.Optional;

public class MudRolling extends Behavior<BarnyardPig> {

    public MudRolling() {
        super(ImmutableMap.of(BarnyardMemoryModules.MUD_ROLLING_COOLDOWN_TICKS, MemoryStatus.VALUE_ABSENT, BarnyardMemoryModules.NEAREST_MUD, MemoryStatus.VALUE_PRESENT, BarnyardMemoryModules.MUD_ROLLING_TICKS, MemoryStatus.VALUE_ABSENT, MemoryModuleType.IS_SNIFFING, MemoryStatus.VALUE_ABSENT, MemoryModuleType.BREED_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.IS_PANICKING, MemoryStatus.VALUE_ABSENT));
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel serverLevel, BarnyardPig entity) {
        return this.getNearestCluster(entity).isPresent();
    }

    @Override
    protected boolean canStillUse(ServerLevel world, BarnyardPig entity, long l) {
        Optional<Integer> memory = entity.getBrain().getMemory(BarnyardMemoryModules.MUD_ROLLING_TICKS);
        if (memory.isPresent() && memory.get() > 0) {
            return false;
        }
        return this.getNearestCluster(entity).map(world::getBlockState).map(BlockBehaviour.BlockStateBase::getBlock).filter(Blocks.MUD::equals).isPresent();
    }

    @Override
    protected void start(ServerLevel serverLevel, BarnyardPig livingEntity, long l) {
        this.getNearestCluster(livingEntity).ifPresent(blockPos -> {
            BehaviorUtils.setWalkAndLookTargetMemories(livingEntity, blockPos, 1.0F, 0);
            int distance = blockPos.distManhattan(livingEntity.blockPosition());
            boolean flag = distance <= 1;
            if (flag) {
                livingEntity.getBrain().setMemory(BarnyardMemoryModules.MUD_ROLLING_TICKS, 100);
            }
        });
    }

    private Optional<BlockPos> getNearestCluster(BarnyardPig entity) {
        return entity.getBrain().getMemory(BarnyardMemoryModules.NEAREST_MUD);
    }

}
