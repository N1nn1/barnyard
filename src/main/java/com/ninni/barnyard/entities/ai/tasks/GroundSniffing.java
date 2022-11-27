package com.ninni.barnyard.entities.ai.tasks;

import com.google.common.collect.ImmutableMap;
import com.ninni.barnyard.entities.BarnyardPig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Unit;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class GroundSniffing extends Behavior<BarnyardPig> {
    private static final IntProvider SNIFF_COOLDOWN = UniformInt.of(100, 200);

    public GroundSniffing() {
        super(ImmutableMap.of(MemoryModuleType.SNIFF_COOLDOWN, MemoryStatus.VALUE_ABSENT));
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel serverLevel, BarnyardPig livingEntity) {
        return livingEntity.getBlockStateOn().is(BlockTags.DIRT) && super.checkExtraStartConditions(serverLevel, livingEntity);
    }

    @Override
    protected void start(ServerLevel serverLevel, BarnyardPig warden, long l) {
        Brain<BarnyardPig> brain = warden.getBrain();
        brain.setMemory(MemoryModuleType.IS_SNIFFING, Unit.INSTANCE);
        brain.setMemoryWithExpiry(MemoryModuleType.SNIFF_COOLDOWN, Unit.INSTANCE, SNIFF_COOLDOWN.sample(serverLevel.getRandom()));
        brain.eraseMemory(MemoryModuleType.WALK_TARGET);
        warden.setPose(Pose.SNIFFING);
    }

}
