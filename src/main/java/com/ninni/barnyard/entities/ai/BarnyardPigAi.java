package com.ninni.barnyard.entities.ai;

import java.util.Optional;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import com.ninni.barnyard.entities.BarnyardPig;
import com.ninni.barnyard.entities.ai.tasks.CalmDown;
import com.ninni.barnyard.entities.ai.tasks.StartMudRolling;
import com.ninni.barnyard.entities.ai.tasks.StartSniffing;
import com.ninni.barnyard.entities.ai.tasks.TickMudRolling;
import com.ninni.barnyard.entities.ai.tasks.TickSniffing;
import com.ninni.barnyard.init.BarnyardActivities;
import com.ninni.barnyard.init.BarnyardEntityTypes;
import com.ninni.barnyard.init.BarnyardMemoryModules;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.crafting.Ingredient;

public class BarnyardPigAi {

    public static final int SNIFFING_DURATION = 120;
    public static final int MUD_ROLL_DURATION = 110;

    public static final UniformInt SNIFFING_COOLDOWN = UniformInt.of(900 * 20, 1500 * 20);
    public static final UniformInt MUD_ROLLING_COOLDOWN = UniformInt.of(60 * 20, 240 * 20);

    protected static final float FAST_SPEED = 1.5F;

    public static Brain<?> makeBrain(Brain<BarnyardPig> brain) {
        initCoreActivity(brain);
        initIdleActivity(brain);
        initFightActivity(brain);
        initSniffingActivity(brain);
        initMudRollingActivity(brain);
        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();
        return brain;
    }

    private static void initMudRollingActivity(Brain<BarnyardPig> brain) {
        brain.addActivityAndRemoveMemoryWhenStopped(BarnyardActivities.MUD_ROLLING, 5, ImmutableList.of(
                new TickMudRolling(MUD_ROLL_DURATION)
        ), BarnyardMemoryModules.IS_ROLLING_IN_MUD);
    }

    private static void initSniffingActivity(Brain<BarnyardPig> brain) {
        brain.addActivityAndRemoveMemoryWhenStopped(Activity.SNIFF, 5, ImmutableList.of(
                new TickSniffing(SNIFFING_DURATION)
        ), MemoryModuleType.IS_SNIFFING);
    }

    private static void initCoreActivity(Brain<BarnyardPig> brain) {
        brain.addActivity(Activity.CORE, 0, ImmutableList.of(
                new Swim(0.8f),
                new CalmDown(48),
                new LookAtTargetSink(45, 90),
                new MoveToTargetSink(),
                new StopBeingAngryIfTargetDead<>(),
                new CountDownCooldownTicks(MemoryModuleType.TEMPTATION_COOLDOWN_TICKS),
                new CountDownCooldownTicks(BarnyardMemoryModules.PIG_SNIFFING_TICKS),
                new CountDownCooldownTicks(BarnyardMemoryModules.MUD_ROLLING_TICKS)
        ));
    }

    private static void initIdleActivity(Brain<BarnyardPig> brain) {
        brain.addActivityWithConditions(Activity.IDLE, ImmutableList.of(
                Pair.of(0, new StartAttacking<>(BarnyardPigAi::getAttackTarget)),
                Pair.of(1, new RunIf<>((mob) -> !mob.hasTusk(), SetWalkTargetAwayFrom.entity(MemoryModuleType.HURT_BY_ENTITY, FAST_SPEED, 20, true))),
                Pair.of(2, new AnimalMakeLove(BarnyardEntityTypes.PIG, 1)),
                Pair.of(3, new FollowTemptation(livingEntity -> 1.25f)),
                Pair.of(4, new BabyFollowAdult<>(UniformInt.of(5, 16), 1.25f)),
                Pair.of(5, new RunIf<>(BarnyardPigAi::canPerformIdleActivies, new RunOne<>(ImmutableList.of(
                    Pair.of(new StartSniffing(), 0),
                    Pair.of(new StartMudRolling(), 0)))
                )),
                Pair.of(6, new RunSometimes<LivingEntity>(new SetEntityLookTarget(EntityType.PLAYER, 6), UniformInt.of(30, 60))),
                Pair.of(7, new RunOne<>(ImmutableList.of(
                    Pair.of(new RandomStroll(1), 2),
                    Pair.of(new SetWalkTargetFromLookTarget(1, 3), 2),
                    Pair.of(new DoNothing(30, 60), 1)
                )))),
                ImmutableSet.of(Pair.of(MemoryModuleType.IS_SNIFFING, MemoryStatus.VALUE_ABSENT), Pair.of(BarnyardMemoryModules.IS_ROLLING_IN_MUD, MemoryStatus.VALUE_ABSENT)));
    }

    private static void initFightActivity(Brain<BarnyardPig> brain) {
        brain.addActivityAndRemoveMemoryWhenStopped(Activity.FIGHT, 10, ImmutableList.of(
            new SetWalkTargetFromAttackTargetIfTargetOutOfReach(FAST_SPEED),
            new MeleeAttack(25),
            new StopAttackingIfTargetInvalid<>()
        ), MemoryModuleType.ATTACK_TARGET);
    }

    protected static boolean canPerformIdleActivies(BarnyardPig pig) {
        Brain<BarnyardPig> brain = pig.getBrain();
        if (pig.isBaby()) return false;
        if (pig.isVehicle()) return false;
        if (pig.isInLove()) return false;
        if (brain.checkMemory(MemoryModuleType.HURT_BY_ENTITY, MemoryStatus.VALUE_PRESENT)) return false;
        return true;
    }

    protected static Optional<? extends LivingEntity> getAttackTarget(BarnyardPig mob) {
        Optional<LivingEntity> target = BehaviorUtils.getLivingEntityFromUUIDMemory(mob, MemoryModuleType.ANGRY_AT);
        boolean canAttack = mob.getHealth() < (mob.getMaxHealth() / 2) || mob.hasTusk();
        if (target.isPresent() && target.get() instanceof Player && mob.isSaddled()) return Optional.empty();
        if (target.isPresent() && canAttack && Sensor.isEntityAttackableIgnoringLineOfSight(mob, target.get())) return target;
        return Optional.empty();
    }

    public static void updateActivity(BarnyardPig pig) {
        pig.getBrain().setActiveActivityToFirstValid(ImmutableList.of(Activity.SNIFF, BarnyardActivities.MUD_ROLLING, Activity.FIGHT, Activity.IDLE));
    }

    public static Ingredient getTemptations() {
        return Ingredient.of(BarnyardTags.PIG_TEMPTS);
    }
}
