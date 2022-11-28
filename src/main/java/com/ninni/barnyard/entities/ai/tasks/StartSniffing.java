package com.ninni.barnyard.entities.ai.tasks;

import com.google.common.collect.ImmutableMap;
import com.ninni.barnyard.entities.BarnyardPig;
import com.ninni.barnyard.entities.ai.BarnyardPigAi;
import com.ninni.barnyard.init.BarnyardMemoryModules;
import com.ninni.barnyard.init.BarnyardSounds;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.level.block.Blocks;

public class StartSniffing extends Behavior<BarnyardPig> {
    public StartSniffing() {
        super(ImmutableMap.of(MemoryModuleType.SNIFF_COOLDOWN, MemoryStatus.VALUE_ABSENT));
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, BarnyardPig pig) {
        return !pig.getBlockStateOn().is(Blocks.MUD) && pig.getBlockStateOn().is(BlockTags.DIRT);
    }

    @Override
    protected void start(ServerLevel level, BarnyardPig pig, long l) {
        Brain<BarnyardPig> brain = pig.getBrain();
        
        brain.setMemory(MemoryModuleType.IS_SNIFFING, Unit.INSTANCE);
        brain.setMemory(BarnyardMemoryModules.PIG_SNIFFING_TICKS, BarnyardPigAi.SNIFFING_DURATION);
        brain.eraseMemory(MemoryModuleType.WALK_TARGET);

        pig.setPose(Pose.SNIFFING);
        pig.playSound(BarnyardSounds.PIG_SNIFF, 1, 1);
    }
}