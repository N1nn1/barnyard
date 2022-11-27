package com.ninni.barnyard.entities;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import com.ninni.barnyard.entities.ai.BarnyardPigAi;
import com.ninni.barnyard.init.BarnyardEntityTypes;
import com.ninni.barnyard.init.BarnyardSensorTypes;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class BarnyardPig extends Animal {
    protected static final ImmutableList<SensorType<? extends Sensor<? super BarnyardPig>>> SENSOR_TYPES = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.NEAREST_ITEMS, SensorType.NEAREST_ADULT, SensorType.HURT_BY, BarnyardSensorTypes.PIG_TEMPTATIONS);
    protected static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(MemoryModuleType.LOOK_TARGET, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryModuleType.WALK_TARGET, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.PATH, MemoryModuleType.ATE_RECENTLY, MemoryModuleType.BREED_TARGET, MemoryModuleType.TEMPTING_PLAYER, MemoryModuleType.NEAREST_VISIBLE_ADULT, MemoryModuleType.TEMPTATION_COOLDOWN_TICKS, MemoryModuleType.IS_TEMPTED, MemoryModuleType.IS_PANICKING);

    public BarnyardPig(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 10.0).add(Attributes.MOVEMENT_SPEED, 0.2f);
    }

    @Override
    protected Brain.Provider<BarnyardPig> brainProvider() {
        return Brain.provider(MEMORY_TYPES, SENSOR_TYPES);
    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> dynamic) {
        return BarnyardPigAi.makeBrain(this.brainProvider().makeBrain(dynamic));
    }

    @Override
    public Brain<BarnyardPig> getBrain() {
        return (Brain<BarnyardPig>) super.getBrain();
    }

    @Override
    protected void customServerAiStep() {
        this.level.getProfiler().push("barnyardPigBrain");
        this.getBrain().tick((ServerLevel)this.level, this);
        this.level.getProfiler().pop();
        this.level.getProfiler().push("barnyardPigActivityUpdate");
        BarnyardPigAi.updateActivity(this);
        this.level.getProfiler().pop();
        super.customServerAiStep();
    }

    @Override
    protected void sendDebugPackets() {
        super.sendDebugPackets();
        DebugPackets.sendEntityBrain(this);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return BarnyardEntityTypes.PIG.create(serverLevel);
    }

}
