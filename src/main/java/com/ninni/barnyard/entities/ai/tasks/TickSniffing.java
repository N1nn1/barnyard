package com.ninni.barnyard.entities.ai.tasks;

import com.google.common.collect.ImmutableMap;
import com.ninni.barnyard.Barnyard;
import com.ninni.barnyard.entities.BarnyardPig;
import com.ninni.barnyard.init.BarnyardItems;
import com.ninni.barnyard.init.BarnyardMemoryModules;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.Vec3;

public class TickSniffing extends Behavior<BarnyardPig> {

    public TickSniffing(int duration) {
        super(ImmutableMap.of(MemoryModuleType.IS_SNIFFING, MemoryStatus.VALUE_PRESENT, MemoryModuleType.SNIFF_COOLDOWN, MemoryStatus.VALUE_ABSENT, BarnyardMemoryModules.PIG_SNIFFING_TICKS, MemoryStatus.VALUE_PRESENT, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED), duration);
    }

    @Override
    protected boolean canStillUse(ServerLevel level, BarnyardPig pig, long l) {
        return true;
    }

    @Override
    protected void tick(ServerLevel level, BarnyardPig pig, long l) {
        var memory = pig.getBrain().getMemory(BarnyardMemoryModules.PIG_SNIFFING_TICKS);
        if (memory.isPresent()) {
            int time = memory.get();
            if (time == 38) {
                Vec3 look = pig.getLookAngle().multiply(0.6, 0, 0.6);
                ItemEntity item = new ItemEntity(level, pig.getX() + look.x(), pig.getY() + 0.2, pig.getZ() + look.z(), BarnyardItems.TRUFFLE.getDefaultInstance());
                item.setDefaultPickUpDelay();
                item.setDeltaMovement(look.multiply(0.3, 0, 0.3).add(0, 0.15, 0));
                level.addFreshEntity(item);
            }
        }
        Barnyard.LOGGER.info(pig.getBrain().getMemory(BarnyardMemoryModules.PIG_SNIFFING_TICKS));
    }

    @Override
    protected void stop(ServerLevel level, BarnyardPig pig, long l) {
        pig.setPose(Pose.STANDING);
        pig.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
        pig.getBrain().eraseMemory(MemoryModuleType.IS_SNIFFING);
        pig.getBrain().setMemoryWithExpiry(MemoryModuleType.SNIFF_COOLDOWN, Unit.INSTANCE, 6000L);
    }
}