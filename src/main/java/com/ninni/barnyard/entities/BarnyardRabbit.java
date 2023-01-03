package com.ninni.barnyard.entities;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;

public class BarnyardRabbit extends Rabbit {

    public BarnyardRabbit(EntityType<? extends Rabbit> entityType, Level level) {
        super(entityType, level);
    }
 
    
    public static boolean checkSpawnRules(EntityType<? extends LivingEntity> entityType, ServerLevelAccessor serverLevelAccessor, MobSpawnType mobSpawnType, BlockPos blockPos, RandomSource randomSource) {
        List<BarnyardRabbit> pigs = serverLevelAccessor.getEntitiesOfClass(BarnyardRabbit.class, new AABB(blockPos).inflate(64));
        boolean flag = true;
        for (BarnyardRabbit pig : pigs) {
            if (pig.blockPosition().closerThan(blockPos, 10)) continue;
            flag = pigs.size() == 0;
        }
        return serverLevelAccessor.getLevel().isDay() && Animal.isBrightEnoughToSpawn(serverLevelAccessor, blockPos) && flag && serverLevelAccessor.getBlockState(blockPos.below()).is(BlockTags.ANIMALS_SPAWNABLE_ON);
    }
}