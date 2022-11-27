package com.ninni.barnyard.entities.ai;

import java.util.Optional;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import com.ninni.barnyard.entities.BarnyardPig;
import com.ninni.barnyard.entities.ai.tasks.CalmDown;
import com.ninni.barnyard.entities.ai.tasks.GroundSniffing;
import com.ninni.barnyard.entities.ai.tasks.PopItemFromGround;
import com.ninni.barnyard.init.BarnyardEntityTypes;
import com.ninni.barnyard.init.BarnyardTags;

import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.AnimalMakeLove;
import net.minecraft.world.entity.ai.behavior.BabyFollowAdult;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.behavior.CountDownCooldownTicks;
import net.minecraft.world.entity.ai.behavior.DoNothing;
import net.minecraft.world.entity.ai.behavior.EraseMemoryIf;
import net.minecraft.world.entity.ai.behavior.FollowTemptation;
import net.minecraft.world.entity.ai.behavior.LookAtTargetSink;
import net.minecraft.world.entity.ai.behavior.MeleeAttack;
import net.minecraft.world.entity.ai.behavior.MoveToTargetSink;
import net.minecraft.world.entity.ai.behavior.RandomStroll;
import net.minecraft.world.entity.ai.behavior.RunIf;
import net.minecraft.world.entity.ai.behavior.RunOne;
import net.minecraft.world.entity.ai.behavior.RunSometimes;
import net.minecraft.world.entity.ai.behavior.SetEntityLookTarget;
import net.minecraft.world.entity.ai.behavior.SetWalkTargetAwayFrom;
import net.minecraft.world.entity.ai.behavior.SetWalkTargetFromAttackTargetIfTargetOutOfReach;
import net.minecraft.world.entity.ai.behavior.SetWalkTargetFromLookTarget;
import net.minecraft.world.entity.ai.behavior.StartAttacking;
import net.minecraft.world.entity.ai.behavior.StopAttackingIfTargetInvalid;
import net.minecraft.world.entity.ai.behavior.StopBeingAngryIfTargetDead;
import net.minecraft.world.entity.ai.behavior.Swim;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.crafting.Ingredient;

public class BarnyardPigAi {

    protected static final float FAST_SPEED = 1.5F;

    public static Brain<?> makeBrain(Brain<BarnyardPig> brain) {
        initCoreActivity(brain);
        initIdleActivity(brain);
        initFightActivity(brain);
        initSniffingActivity(brain);
        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();
        return brain;
    }

    private static void initSniffingActivity(Brain<BarnyardPig> brain) {
        brain.addActivityAndRemoveMemoryWhenStopped(Activity.SNIFF, 5, ImmutableList.of(new PopItemFromGround(100)), MemoryModuleType.IS_SNIFFING);
    }

    private static void initCoreActivity(Brain<BarnyardPig> brain) {
        brain.addActivity(Activity.CORE, 0, ImmutableList.of(
                new Swim(0.8f),
                new CalmDown(),
                new EraseMemoryIf<BarnyardPig>((mob) -> mob.getBrain().hasMemoryValue(MemoryModuleType.IS_PANICKING), MemoryModuleType.IS_SNIFFING),
                new RunIf<BarnyardPig>((mob) -> !mob.hasTusk(), SetWalkTargetAwayFrom.entity(MemoryModuleType.HURT_BY_ENTITY, FAST_SPEED, 20, true)),
                new LookAtTargetSink(45, 90),
                new MoveToTargetSink(),
                new StopBeingAngryIfTargetDead<BarnyardPig>(),
                new CountDownCooldownTicks(MemoryModuleType.TEMPTATION_COOLDOWN_TICKS)
        ));
    }

    private static void initIdleActivity(Brain<BarnyardPig> brain) {
        brain.addActivityWithConditions(Activity.IDLE, ImmutableList.of(
                Pair.of(0, new StartAttacking<BarnyardPig>(BarnyardPigAi::getAttackTarget)),
                Pair.of(1, new RunSometimes<LivingEntity>(new SetEntityLookTarget(EntityType.PLAYER, 6.0f), UniformInt.of(30, 60))),
                Pair.of(2, new AnimalMakeLove(BarnyardEntityTypes.PIG, 1.0f)),
                Pair.of(3, new FollowTemptation(livingEntity -> 1.25f)),
                Pair.of(4, new BabyFollowAdult<>(UniformInt.of(5, 16), 1.25f)),
                Pair.of(5, new GroundSniffing()),
                Pair.of(6, new RunOne<>(ImmutableMap.of(MemoryModuleType.IS_SNIFFING, MemoryStatus.VALUE_ABSENT),
                        ImmutableList.of(
                                Pair.of(new RandomStroll(1.0f), 2),
                                Pair.of(new SetWalkTargetFromLookTarget(1.0f, 3), 2),
                                Pair.of(new DoNothing(30, 60), 1)
                        )))),
                ImmutableSet.of(Pair.of(MemoryModuleType.IS_SNIFFING, MemoryStatus.VALUE_ABSENT)));
    }

    private static void initFightActivity(Brain<BarnyardPig> brain) {
        brain.addActivityAndRemoveMemoryWhenStopped(Activity.FIGHT, 10, ImmutableList.of(
            new SetWalkTargetFromAttackTargetIfTargetOutOfReach(FAST_SPEED),
            new MeleeAttack(25),
            new StopAttackingIfTargetInvalid<BarnyardPig>()
        ), MemoryModuleType.ATTACK_TARGET);
    }

    public static Optional<? extends LivingEntity> getAttackTarget(BarnyardPig mob) {
        Optional<LivingEntity> target = BehaviorUtils.getLivingEntityFromUUIDMemory(mob, MemoryModuleType.ANGRY_AT);
        boolean canAttack = mob.getHealth() < (mob.getMaxHealth() / 2) || mob.hasTusk();
        if (target.isPresent() && canAttack && Sensor.isEntityAttackableIgnoringLineOfSight(mob, target.get())) {
            return target;
        } else {
            return Optional.empty();
        }
    }

    public static void updateActivity(BarnyardPig pig) {
        pig.getBrain().setActiveActivityToFirstValid(ImmutableList.of(Activity.FIGHT, Activity.IDLE, Activity.SNIFF));
    }

    public static Ingredient getTemptations() {
        return Ingredient.of(BarnyardTags.PIG_TEMPTS);
    }
}
