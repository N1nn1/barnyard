package com.ninni.barnyard.entities.ai.sensors;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.ninni.barnyard.entities.BarnyardPig;
import com.ninni.barnyard.init.BarnyardMemoryModules;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.level.block.Blocks;

public class NearestMudSensor extends Sensor<BarnyardPig> {

    @Override
    protected void doTick(ServerLevel level, BarnyardPig mob) {
        Brain<BarnyardPig> brain = mob.getBrain();
        MemoryModuleType<BlockPos> memory = BarnyardMemoryModules.NEAREST_MUD;

        if (brain.hasMemoryValue(memory)) {
            Optional<BlockPos> optional = brain.getMemory(memory);
            if (optional.isPresent()) {
                BlockPos pos = optional.get();
                if (pos.distManhattan(mob.blockPosition()) > 16) findNewPosition(level, mob);
            }
        } else {
            findNewPosition(level, mob);
        }
    }

    protected void findNewPosition(ServerLevel level, BarnyardPig mob) {
        Brain<BarnyardPig> brain = mob.getBrain();
        MemoryModuleType<BlockPos> memory = BarnyardMemoryModules.NEAREST_MUD;
        List<BlockPos> list = Lists.newArrayList();

        int range = 8;
        for (int x = -range; x <= range; x++) {
            for (int z = -range; z <= range; z++) {
                for (int y = -range; y <= range; y++) {
                    BlockPos pos = mob.blockPosition().offset(x, y, z);
                    if (level.getBlockState(pos).isAir() && level.getBlockState(pos.below()).is(Blocks.MUD)) {
                        list.add(pos.below());
                    }
                }
            }
        }

        if (!list.isEmpty()) {
            BlockPos pos = list.get(mob.getRandom().nextInt(list.size()));
            brain.setMemory(memory, pos);
        } else {
            brain.eraseMemory(memory);
        }
    }

    @Override
    public Set<MemoryModuleType<?>> requires() {
        return ImmutableSet.of(BarnyardMemoryModules.NEAREST_MUD);
    }
}
