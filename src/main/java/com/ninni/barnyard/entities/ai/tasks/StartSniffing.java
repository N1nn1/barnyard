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
    protected boolean checkExtraStartConditions(ServerLevel level, BarnyardPig mob) {
        return !mob.getBlockStateOn().is(Blocks.MUD) && mob.getBlockStateOn().is(BlockTags.DIRT);
    }

    @Override
    protected void start(ServerLevel level, BarnyardPig mob, long l) {
        Brain<BarnyardPig> brain = mob.getBrain();
        
        brain.setMemory(MemoryModuleType.IS_SNIFFING, Unit.INSTANCE);
        brain.setMemory(BarnyardMemoryModules.PIG_SNIFFING_TICKS, BarnyardPigAi.SNIFFING_DURATION);
        brain.eraseMemory(MemoryModuleType.WALK_TARGET);

        mob.setPose(Pose.SNIFFING);
        mob.playSound(BarnyardSounds.PIG_SNIFF, 1, 1);
    }
}