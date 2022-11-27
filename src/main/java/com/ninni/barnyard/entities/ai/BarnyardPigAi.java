package com.ninni.barnyard.entities.ai;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import com.ninni.barnyard.entities.BarnyardPig;
import com.ninni.barnyard.entities.ai.tasks.GroundSniffing;
import com.ninni.barnyard.entities.ai.tasks.PopItemFromGround;
import com.ninni.barnyard.init.BarnyardEntityTypes;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.AnimalMakeLove;
import net.minecraft.world.entity.ai.behavior.AnimalPanic;
import net.minecraft.world.entity.ai.behavior.BabyFollowAdult;
import net.minecraft.world.entity.ai.behavior.CountDownCooldownTicks;
import net.minecraft.world.entity.ai.behavior.DoNothing;
import net.minecraft.world.entity.ai.behavior.FollowTemptation;
import net.minecraft.world.entity.ai.behavior.LookAtTargetSink;
import net.minecraft.world.entity.ai.behavior.MoveToTargetSink;
import net.minecraft.world.entity.ai.behavior.RandomStroll;
import net.minecraft.world.entity.ai.behavior.RunOne;
import net.minecraft.world.entity.ai.behavior.RunSometimes;
import net.minecraft.world.entity.ai.behavior.SetEntityLookTarget;
import net.minecraft.world.entity.ai.behavior.SetWalkTargetFromLookTarget;
import net.minecraft.world.entity.ai.behavior.Swim;
import net.minecraft.world.entity.ai.behavior.warden.SetRoarTarget;
import net.minecraft.world.entity.ai.behavior.warden.Sniffing;
import net.minecraft.world.entity.ai.behavior.warden.TryToSniff;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

public class BarnyardPigAi {

    public static Brain<?> makeBrain(Brain<BarnyardPig> brain) {
        initCoreActivity(brain);
        initIdleActivity(brain);
        initSniffingActivity(brain);
        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();
        return brain;
    }

    private static void initSniffingActivity(Brain<BarnyardPig> brain) {
        brain.addActivityAndRemoveMemoryWhenStopped(Activity.SNIFF, 5, ImmutableList.of(
                new PopItemFromGround(100)), MemoryModuleType.IS_SNIFFING);
    }

    private static void initCoreActivity(Brain<BarnyardPig> brain) {
        brain.addActivity(Activity.CORE, 0, ImmutableList.of(
                new Swim(0.8f),
                new AnimalPanic(2.0f),
                new LookAtTargetSink(45, 90),
                new MoveToTargetSink(),
                new CountDownCooldownTicks(MemoryModuleType.TEMPTATION_COOLDOWN_TICKS)
        ));
    }

    private static void initIdleActivity(Brain<BarnyardPig> brain) {
        brain.addActivityWithConditions(Activity.IDLE, ImmutableList.of(
                Pair.of(0, new RunSometimes<LivingEntity>(new SetEntityLookTarget(EntityType.PLAYER, 6.0f), UniformInt.of(30, 60))),
                Pair.of(1, new AnimalMakeLove(BarnyardEntityTypes.PIG, 1.0f)),
                Pair.of(2, new FollowTemptation(livingEntity -> 1.25f)),
                Pair.of(3, new BabyFollowAdult<>(UniformInt.of(5, 16), 1.25f)),
                Pair.of(4, new GroundSniffing()),
                Pair.of(5, new RunOne<>(ImmutableMap.of(MemoryModuleType.IS_SNIFFING, MemoryStatus.VALUE_ABSENT),
                        ImmutableList.of(
                                Pair.of(new RandomStroll(1.0f), 2),
                                Pair.of(new SetWalkTargetFromLookTarget(1.0f, 3), 2),
                                Pair.of(new DoNothing(30, 60), 1)
                        )))),
                ImmutableSet.of(Pair.of(MemoryModuleType.IS_SNIFFING, MemoryStatus.VALUE_ABSENT)));
    }

    public static void updateActivity(BarnyardPig pig) {
        pig.getBrain().setActiveActivityToFirstValid(ImmutableList.of(Activity.IDLE, Activity.SNIFF));
    }

    public static Ingredient getTemptations() {
        return Ingredient.of(Items.CARROT);
    }
}
