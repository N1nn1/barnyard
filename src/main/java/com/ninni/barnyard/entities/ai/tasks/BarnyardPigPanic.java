package com.ninni.barnyard.entities.ai.tasks;

import com.ninni.barnyard.entities.BarnyardPig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.AnimalPanic;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class BarnyardPigPanic extends AnimalPanic {
    public BarnyardPigPanic(float f) {
        super(f);
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel serverLevel, PathfinderMob livingEntity) {
        if (livingEntity instanceof BarnyardPig pig && (pig.hasTusk() || pig.getBrain().hasMemoryValue(MemoryModuleType.IS_SNIFFING))) return false;
        return super.checkExtraStartConditions(serverLevel, livingEntity);
    }

    @Override
    protected boolean canStillUse(ServerLevel serverLevel, PathfinderMob pathfinderMob, long l) {
        if (pathfinderMob instanceof BarnyardPig pig && (pig.hasTusk() || pig.getBrain().hasMemoryValue(MemoryModuleType.IS_SNIFFING))) return false;
        return super.canStillUse(serverLevel, pathfinderMob, l);
    }
}
