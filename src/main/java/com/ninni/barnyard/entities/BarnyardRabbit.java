package com.ninni.barnyard.entities;

import java.util.List;

import com.ninni.barnyard.init.BarnyardEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
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

    private int getRandomRabbitType(LevelAccessor levelAccessor) {
        Holder<Biome> holder = levelAccessor.getBiome(this.blockPosition());
        int i = levelAccessor.getRandom().nextInt(100);
        if (holder.value().getPrecipitation() == Biome.Precipitation.SNOW) {
            return i < 80 ? 1 : 3;
        }
        if (holder.is(BiomeTags.ONLY_ALLOWS_SNOW_AND_GOLD_RABBITS)) {
            return 4;
        }
        return i < 50 ? 0 : (i < 90 ? 5 : 2);
    }

    @Override
    public boolean causeFallDamage(float f, float g, DamageSource damageSource) {
        return false;
    }

    @Override
    public BarnyardRabbit getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        BarnyardRabbit rabbit = BarnyardEntityTypes.RABBIT.create(serverLevel);
        int i = this.getRandomRabbitType(serverLevel);
        if (this.random.nextInt(20) != 0) {
            i = ageableMob instanceof Rabbit && this.random.nextBoolean() ? ((Rabbit)ageableMob).getRabbitType() : this.getRabbitType();
        }
        assert rabbit != null;
        rabbit.setRabbitType(i);
        return rabbit;
    }
}