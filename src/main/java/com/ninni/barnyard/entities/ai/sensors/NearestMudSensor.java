package com.ninni.barnyard.entities.ai.sensors;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.ninni.barnyard.entities.BarnyardPig;
import com.ninni.barnyard.init.BarnyardMemoryModules;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class NearestMudSensor extends Sensor<BarnyardPig> {

    @Override
    protected void doTick(ServerLevel world, BarnyardPig entity) {
        List<BlockPos> poses = Lists.newArrayList();
        int range = 8;
        for (int x = -range; x <= range; x++) {
            for (int z = -range; z <= range; z++) {
                for (int y = -range; y <= range; y++) {
                    BlockPos pos = new BlockPos(entity.getX() + x, entity.getY() + y, entity.getZ() + z);
                    BlockState state = world.getBlockState(pos);
                    BlockState belowState = world.getBlockState(pos.below());
                    if (state.isAir() && belowState.is(Blocks.MUD)) {
                        poses.add(pos.below());
                    }
                }
            }
        }
        if (!poses.isEmpty()) {
            poses.sort(Comparator.comparingDouble(entity.blockPosition()::distSqr));
            for (BlockPos pos : poses) {
                if (!entity.getBrain().hasMemoryValue(BarnyardMemoryModules.NEAREST_MUD)) {
                    entity.getBrain().setMemory(BarnyardMemoryModules.NEAREST_MUD, pos);
                }
            }
        } else {
            entity.getBrain().eraseMemory(BarnyardMemoryModules.NEAREST_MUD);
        }
    }

    @Override
    public Set<MemoryModuleType<?>> requires() {
        return ImmutableSet.of(BarnyardMemoryModules.NEAREST_MUD);
    }

}
