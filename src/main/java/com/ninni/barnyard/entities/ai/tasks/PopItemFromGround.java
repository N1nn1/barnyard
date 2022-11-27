package com.ninni.barnyard.entities.ai.tasks;

import com.google.common.collect.ImmutableMap;
import com.ninni.barnyard.entities.BarnyardPig;
import com.ninni.barnyard.init.BarnyardSounds;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class PopItemFromGround extends Behavior<BarnyardPig> {

    public PopItemFromGround(int i) {
        super(ImmutableMap.of(MemoryModuleType.IS_SNIFFING, MemoryStatus.VALUE_PRESENT, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.SNIFF_COOLDOWN, MemoryStatus.REGISTERED), i);
    }

    @Override
    protected boolean canStillUse(ServerLevel serverLevel, BarnyardPig pig, long l) {
        return true;
    }

    @Override
    protected void start(ServerLevel serverLevel, BarnyardPig pig, long l) {
        pig.playSound(BarnyardSounds.PIG_SNIFF, 1, 1.0f);
    }

    @Override
    protected void stop(ServerLevel serverLevel, BarnyardPig pig, long l) {
        if (pig.hasPose(Pose.SNIFFING)) {
            pig.setPose(Pose.STANDING);
        }
        pig.spawnAtLocation(new ItemStack(Items.DIAMOND));
        pig.getBrain().eraseMemory(MemoryModuleType.IS_SNIFFING);
        pig.getBrain().setMemoryWithExpiry(MemoryModuleType.SNIFF_COOLDOWN, Unit.INSTANCE, 6000L);
        pig.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
    }
}
