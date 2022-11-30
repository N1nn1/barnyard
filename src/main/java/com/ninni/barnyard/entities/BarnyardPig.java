package com.ninni.barnyard.entities;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import com.ninni.barnyard.entities.ai.BarnyardPigAi;
import com.ninni.barnyard.init.BarnyardEntityTypes;
import com.ninni.barnyard.init.BarnyardMemoryModules;
import com.ninni.barnyard.init.BarnyardParticleTypes;
import com.ninni.barnyard.init.BarnyardSensorTypes;
import com.ninni.barnyard.init.BarnyardSounds;
import com.ninni.barnyard.init.BarnyardTags;

import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Unit;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ItemBasedSteering;
import net.minecraft.world.entity.ItemSteerable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.Saddleable;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class BarnyardPig extends AbstractHappyAnimal implements Saddleable, ItemSteerable, CooldownRideableJumping {

    protected static final ImmutableList<SensorType<? extends Sensor<? super BarnyardPig>>> SENSOR_TYPES = ImmutableList.of(
            SensorType.NEAREST_LIVING_ENTITIES,
            SensorType.NEAREST_PLAYERS,
            SensorType.NEAREST_ITEMS,
            SensorType.NEAREST_ADULT,
            SensorType.HURT_BY,
            BarnyardSensorTypes.PIG_TEMPTATIONS,
            BarnyardSensorTypes.NEAREST_MUD_SENSOR
    );

    protected static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(
            MemoryModuleType.LOOK_TARGET,
            MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES,
            MemoryModuleType.WALK_TARGET,
            MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
            MemoryModuleType.PATH,
            MemoryModuleType.ATTACK_TARGET,
            MemoryModuleType.ATTACK_COOLING_DOWN,
            MemoryModuleType.ANGRY_AT,
            MemoryModuleType.NEAREST_ATTACKABLE,
            MemoryModuleType.ATE_RECENTLY,
            MemoryModuleType.BREED_TARGET,
            MemoryModuleType.NEAREST_LIVING_ENTITIES,
            MemoryModuleType.NEAREST_VISIBLE_PLAYER,
            MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES,
            MemoryModuleType.HURT_BY_ENTITY,
            MemoryModuleType.TEMPTING_PLAYER,
            MemoryModuleType.NEAREST_VISIBLE_ADULT,
            MemoryModuleType.TEMPTATION_COOLDOWN_TICKS,
            MemoryModuleType.IS_TEMPTED,
            MemoryModuleType.IS_PANICKING,

            MemoryModuleType.IS_SNIFFING,
            MemoryModuleType.SNIFF_COOLDOWN,
            BarnyardMemoryModules.PIG_SNIFFING_TICKS,

            BarnyardMemoryModules.IS_ROLLING_IN_MUD,
            BarnyardMemoryModules.MUD_COOLDOWN,
            BarnyardMemoryModules.MUD_ROLLING_TICKS,
            BarnyardMemoryModules.NEAREST_MUD,

            BarnyardMemoryModules.IS_SLEEPING
    );

    private static final EntityDataAccessor<Boolean> TUSK = SynchedEntityData.defineId(BarnyardPig.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_SADDLE_ID = SynchedEntityData.defineId(BarnyardPig.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> CHARGING = SynchedEntityData.defineId(BarnyardPig.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_BOOST_TIME = SynchedEntityData.defineId(BarnyardPig.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> MUDDY = SynchedEntityData.defineId(BarnyardPig.class, EntityDataSerializers.INT);

    private final ItemBasedSteering steering;
    private int chargingCooldown = 0;
    protected float playerJumpPendingScale;
    public final AnimationState sniffingAnimationState = new AnimationState();
    public final AnimationState rollingInMudAnimationState = new AnimationState();
    public final AnimationState sleepingAnimationState = new AnimationState();

    public BarnyardPig(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
        steering = new ItemBasedSteering(entityData, DATA_BOOST_TIME, DATA_SADDLE_ID);
    }

    @Override
    public void travel(Vec3 input) {
        if (!isAlive()) return;
        if (!hasPose(Pose.STANDING) && isOnGround()) {
            setDeltaMovement(getDeltaMovement().multiply(0, 1, 0));
            input = input.multiply(0, 1, 0);
        }
        float f = getSteeringSpeed();
        boolean flag = playerJumpPendingScale > 0.0F && !isCharging() && onGround;
        if (flag) {
            f += f * 1.15f * Mth.sin((float) steering.boostTime / (float) steering.boostTimeTotal * (float) Math.PI);
            setDeltaMovement(getDeltaMovement().add(getLookAngle().multiply(1, 0, 1).normalize().scale((double) (6.44444F * playerJumpPendingScale) * getAttributeValue(Attributes.MOVEMENT_SPEED) * (double) getBlockSpeedFactor()).add(0, (double) (1.4285f * f) * 1.5F, 0)));
            chargingCooldown = 55;
            setCharging(true);
            hasImpulse = false;
            playerJumpPendingScale = 0.0F;
        }
        travel(this, steering, input);
    }

    @Override
    public void tick() {
        super.tick();
        if (isCharging() && chargingCooldown < 55 && (onGround || isInWater())) setCharging(false);
        if (chargingCooldown > 0) {
            chargingCooldown--;
            if (chargingCooldown >= 49) {
                level.getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(1.2), this::isValidTarget).forEach(this::damageRamTarget);
            }
            if (chargingCooldown == 0) {
                level.playSound(null, getX(), getY(), getZ(), BarnyardSounds.PIG_DASH_RECHARGE, SoundSource.PLAYERS, 1, 1);
            }
        }

        if (getMuddyTicks() > 0) setMuddy(getMuddyTicks() - 1);;
    }

    public static boolean checkBarnyardPigSpawnRules(EntityType<? extends LivingEntity> entityType, ServerLevelAccessor serverLevelAccessor, MobSpawnType mobSpawnType, BlockPos blockPos, RandomSource randomSource) {
        List<BarnyardPig> pigs = serverLevelAccessor.getEntitiesOfClass(BarnyardPig.class, new AABB(blockPos).inflate(64));
        boolean flag = true;
        for (BarnyardPig pig : pigs) {
            if (pig.blockPosition().closerThan(blockPos, 10)) continue;
            flag = pigs.size() == 0;
        }
        return serverLevelAccessor.getLevel().isDay() && Animal.isBrightEnoughToSpawn(serverLevelAccessor, blockPos) && flag && serverLevelAccessor.getBlockState(blockPos.below()).is(BlockTags.ANIMALS_SPAWNABLE_ON);
    }

    @Override
    public void aiStep() {
        if (getMuddyTicks() > 1800 && random.nextInt(5) == 0) {
            for (int i = 0; i < random.nextInt(1) + 1; ++i) {
                level.addParticle(BarnyardParticleTypes.MUD, getRandomX(0.8), getY() + 0.5F, getRandomZ(0.8), 0, random.nextFloat() * 5, 0);
            }
        }
        if (isBaby() && getFeetBlockState().is(Blocks.MUD)) setMuddy(20 * 180);
        if (isMuddy() && isInWaterRainOrBubble()) setMuddy(0);
        super.aiStep();
    }

    private boolean isValidTarget(LivingEntity mob) {
        return mob.isAlive() && !mob.is(this) && !mob.is(getControllingPassenger());
    }

    private void damageRamTarget(LivingEntity mob) {
        if (getControllingPassenger() != null && getControllingPassenger() instanceof LivingEntity passenger) {
            Vec3 vec33 = mob.position().subtract(position().add(0, 1.6, 0)).normalize();
            double d = 0.25 * (1 - mob.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
            double e = (1 - mob.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
            mob.push(vec33.x() * e, vec33.y() * d, vec33.z() * e);
            DamageSource source = passenger instanceof Player player ? DamageSource.playerAttack(player) : DamageSource.mobAttack(passenger);
            if (mob.hurt(source, (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 2F)) {
                level.playSound(null, getX(), getY(), getZ(), BarnyardSounds.PIG_DASH_RAM, SoundSource.PLAYERS, 1, 1);
            }
        }
    }

    public void setAngerTarget(LivingEntity mob) {
        if (!Sensor.isEntityAttackableIgnoringLineOfSight(this, mob)) return;
        getBrain().eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
        getBrain().setMemoryWithExpiry(MemoryModuleType.ANGRY_AT, mob.getUUID(), 600L);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.getDirectEntity() != null && source.getDirectEntity() instanceof LivingEntity mob)
            setAngerTarget(mob);
        return super.hurt(source, amount);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        InteractionResult result = super.mobInteract(player, hand);

        if (isSaddled()) {
            if (stack.is(ConventionalItemTags.SHEARS)) {
                steering.setSaddle(false);
                if (!player.getAbilities().instabuild) stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
                spawnAtLocation(Items.SADDLE);
                playSound(BarnyardSounds.PIG_SADDLE_UNEQUIP, 1, 1);
                return InteractionResult.sidedSuccess(level.isClientSide);
            } else if (isFood(stack)) {
                return result;
            } else if (!isVehicle() && !player.isSecondaryUseActive()) {
                if (!level.isClientSide) player.startRiding(this);
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        } else {
            if (stack.is(Items.SADDLE)) return stack.interactLivingEntity(player, this, hand);
        }

        return result;
    }

    @Override
    protected void usePlayerItem(Player player, InteractionHand hand, ItemStack stack) {
        if (isFood(stack)) playSound(getEatingSound(stack), 1, getVoicePitch());
        super.usePlayerItem(player, hand, stack);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(BarnyardTags.PIG_BREEDS);
    }

    @Override
    public boolean canBeAffected(MobEffectInstance mobEffectInstance) {
        if (mobEffectInstance.getEffect() == MobEffects.POISON) return false;
        return super.canBeAffected(mobEffectInstance);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> entityDataAccessor) {
        if (!firstTick && CHARGING.equals(entityDataAccessor)) {
            chargingCooldown = chargingCooldown == 0 ? 55 : chargingCooldown;
        }
        if (DATA_BOOST_TIME.equals(entityDataAccessor) && level.isClientSide) {
            steering.onSynced();
        }
        if (DATA_POSE.equals(entityDataAccessor)) {
            if (getPose() == Pose.SNIFFING) {
                sniffingAnimationState.start(tickCount);
            } else {
                sniffingAnimationState.stop();
            }
            if (getPose() == Pose.DIGGING) {
                rollingInMudAnimationState.start(tickCount);
            } else {
                rollingInMudAnimationState.stop();
            }
            if (getPose() == Pose.ROARING) {
                sleepingAnimationState.start(tickCount);
            } else {
                sleepingAnimationState.stop();
            }
        }
        super.onSyncedDataUpdated(entityDataAccessor);
    }

    @Nullable
    @Override
    public Entity getControllingPassenger() {
        Entity entity = getFirstPassenger();
        return entity != null && canBeControlledBy(entity) ? entity : null;
    }

    private boolean canBeControlledBy(Entity entity) {
        if (isSaddled() && entity instanceof Player player) {
            return player.getMainHandItem().is(Items.CARROT_ON_A_STICK) || player.getOffhandItem().is(Items.CARROT_ON_A_STICK);
        }
        return false;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(TUSK, false);
        entityData.define(DATA_SADDLE_ID, false);
        entityData.define(MUDDY, 0);
        entityData.define(CHARGING, false);
        entityData.define(DATA_BOOST_TIME, 0);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putBoolean("Tusk", hasTusk());
        nbt.putInt("Muddy", getMuddyTicks());
        steering.addAdditionalSaveData(nbt);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        setHasTusk(nbt.getBoolean("Tusk"));
        setMuddy(nbt.getInt("Muddy"));
        steering.readAdditionalSaveData(nbt);
    }

    public boolean isCharging()  {
        return entityData.get(CHARGING);
    }

    public void setCharging(boolean charging) {
        entityData.set(CHARGING, charging);
    }

    public boolean isMuddy() {
        return entityData.get(MUDDY) > 0;
    }

    public int getMuddyTicks() {
        return entityData.get(MUDDY);
    }

    public void setMuddy(int muddy) {
        entityData.set(MUDDY, muddy);
    }

    public boolean hasTusk() {
        return entityData.get(TUSK);
    }

    public void setHasTusk(boolean tusk) {
        entityData.set(TUSK, tusk);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType type, @Nullable SpawnGroupData data, @Nullable CompoundTag nbt){
        if (level.getRandom().nextFloat() < 0.2F) setHasTusk(true);

        brain.setMemoryWithExpiry(MemoryModuleType.SNIFF_COOLDOWN, Unit.INSTANCE, BarnyardPigAi.SNIFFING_COOLDOWN.sample(random));
        brain.setMemoryWithExpiry(BarnyardMemoryModules.MUD_COOLDOWN, Unit.INSTANCE, BarnyardPigAi.MUD_ROLLING_COOLDOWN.sample(random));

        return super.finalizeSpawn(level, difficulty, type, data, nbt);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20)
                .add(Attributes.MOVEMENT_SPEED, 0.2f)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5f)
                .add(Attributes.ATTACK_DAMAGE, 4);
    }

    @Override
    protected Brain.Provider<BarnyardPig> brainProvider() {
        return Brain.provider(MEMORY_TYPES, SENSOR_TYPES);
    }

    @Override
    protected Brain<?> makeBrain(Dynamic <?> dynamic) {
        return BarnyardPigAi.makeBrain(brainProvider().makeBrain(dynamic));
    }

    @Override
    @SuppressWarnings("unchecked")
    public Brain<BarnyardPig> getBrain() {
        return (Brain<BarnyardPig>) super.getBrain();
    }

    @Override
    protected void customServerAiStep() {
        level.getProfiler().push("barnyardPigBrain");
        getBrain().tick((ServerLevel) level, this);
        level.getProfiler().pop();
        level.getProfiler().push("barnyardPigActivityUpdate");
        BarnyardPigAi.updateActivity(this);
        level.getProfiler().pop();
        super.customServerAiStep();
    }

    @Override
    protected void sendDebugPackets() {
        super.sendDebugPackets();
        DebugPackets.sendEntityBrain(this);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob spouse) {
        BarnyardPig child = BarnyardEntityTypes.PIG.create(level);
        float chance = 0.1F;
        if (hasTusk()) chance += 0.4F;
        if (spouse instanceof BarnyardPig mob && mob.hasTusk()) chance += 0.4F;
        if (random.nextFloat() < chance) child.setHasTusk(true);
        return child;
    }

    @Override
    public int getMaxHeadYRot() {
        return 1;
    }

    @Override
    public int getMaxHeadXRot() {
        return 1;
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntityDimensions entityDimensions) {
        return super.getStandingEyeHeight(pose, entityDimensions) - 0.3F;
    }

    @Override
    @Nullable
    protected SoundEvent getAmbientSound() {
        return getPose() == Pose.STANDING ? BarnyardSounds.PIG_AMBIENT : null;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource){
        return BarnyardSounds.PIG_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return BarnyardSounds.PIG_DEATH;
    }

    @Override
    public SoundEvent getEatingSound(ItemStack stack) {
        return BarnyardSounds.PIG_EAT;
    }

    protected SoundEvent getStepSound() {
        return isMuddy() || getFeetBlockState().is(Blocks.MUD) ? BarnyardSounds.PIG_STEP_MUDDY : BarnyardSounds.PIG_STEP;
    }

    protected void playStepSound(BlockPos blockPos, BlockState blockState){
        playSound(getStepSound(), 0.15F, 1);
    }

    protected void dropEquipment() {
        super.dropEquipment();
        if (isSaddled()) {
            spawnAtLocation(Items.SADDLE);
        }
    }

    public void positionRider(Entity entity){
        super.positionRider(entity);
        if (hasPassenger(entity)) {
            float f = Mth.cos(yBodyRot * 0.0175F);
            float g = Mth.sin(yBodyRot * 0.0175F);
            entity.setPos(getX() + (0.2F * g), getY() + getPassengersRidingOffset() + entity.getMyRidingOffset() - 0.05F, getZ() - (0.2F * f));
        }
    }

    @Override
    public boolean isSaddleable() {
        return isAlive() && !isBaby();
    }

    @Override
    public void equipSaddle(@Nullable SoundSource soundSource){
        steering.setSaddle(true);
        if (soundSource != null) {
            level.playSound(null, this, BarnyardSounds.PIG_SADDLE_EQUIP, soundSource, 0.5f, 1);
        }
    }

    @Override
    public boolean isSaddled() {
        return steering.hasSaddle();
    }

    @Override
    public boolean boost() {
        return steering.boost(getRandom());
    }

    @Override
    public void travelWithInput(Vec3 input) {
        if (!hasPose(Pose.STANDING)) input = Vec3.ZERO;
        super.travel(input);
    }

   public float getSteeringSpeed() {
       return (float) getAttributeValue(Attributes.MOVEMENT_SPEED) * 0.3f;
   }

    @Override
    public void onPlayerJump(int i) {
        if (!isSaddled() || chargingCooldown > 0  || !this.hasTusk()) return;
        i = Math.max(i, 0);
        playerJumpPendingScale = i >= 90 ? 1 : 0.4f + 0.4f * (float) i / 90;
    }

    @Override
    public boolean canJump() {
        return isSaddled() && this.hasTusk();
    }

    @Override
    public void handleStartJump(int i) {
        if (getJumpCooldown() > 0 || !this.hasTusk()) return;
        playSound(BarnyardSounds.PIG_DASH, 1, 1);
        setCharging(true);
    }

    @Override
    public void handleStopJump() {
    }

    @Override
    public int getJumpCooldown() {
        return chargingCooldown;
    }

    @Override
    public int getMinHappyLevel() {
        return -5;
    }

    @Override
    public int getMaxHappyLevel() {
        return 5;
    }

}
