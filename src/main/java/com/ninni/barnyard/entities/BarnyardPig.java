package com.ninni.barnyard.entities;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import com.ninni.barnyard.entities.ai.BarnyardPigAi;
import com.ninni.barnyard.init.BarnyardEntityTypes;
import com.ninni.barnyard.init.BarnyardMemoryModules;
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
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
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
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class BarnyardPig extends Animal implements Saddleable, ItemSteerable, CooldownRideableJumping {
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
            BarnyardMemoryModules.MUD_ROLLING_COOLDOWN_TICKS,
            BarnyardMemoryModules.MUD_ROLLING_TICKS,
            BarnyardMemoryModules.NEAREST_MUD
    );
    private static final EntityDataAccessor<Boolean> TUSK = SynchedEntityData.defineId(BarnyardPig.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_SADDLE_ID = SynchedEntityData.defineId(BarnyardPig.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> MUDDY = SynchedEntityData.defineId(BarnyardPig.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> CHARGING = SynchedEntityData.defineId(BarnyardPig.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_BOOST_TIME = SynchedEntityData.defineId(BarnyardPig.class, EntityDataSerializers.INT);
    private final ItemBasedSteering steering;
    private int chargingCooldown = 0;
    protected float playerJumpPendingScale;
    public final AnimationState sniffingAnimationState = new AnimationState();
    public final AnimationState rollingInMudAnimationState = new AnimationState();

    public BarnyardPig(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
        steering = new ItemBasedSteering(entityData, DATA_BOOST_TIME, DATA_SADDLE_ID);
    }

    public static boolean canPerformIdleActivies(BarnyardPig pig) {
        Brain<BarnyardPig> brain = pig.getBrain();
        if (pig.isBaby()) return false;
        if (pig.isVehicle()) return false;
        if (pig.isInLove()) return false;
        if (brain.checkMemory(MemoryModuleType.HURT_BY_ENTITY, MemoryStatus.VALUE_PRESENT)) return false;
        if (brain.checkMemory(MemoryModuleType.IS_SNIFFING, MemoryStatus.VALUE_PRESENT)) return false;
        if (brain.checkMemory(BarnyardMemoryModules.MUD_ROLLING_TICKS, MemoryStatus.VALUE_PRESENT)) return false;
        return true;
    }

    @Override
    public void travel(Vec3 vec3) {
        if (!this.isAlive()) return;
        if (this.getBrain().getMemory(BarnyardMemoryModules.MUD_ROLLING_TICKS).isPresent() && this.isOnGround()) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.0, 1.0, 0.0));
            vec3 = vec3.multiply(0.0, 1.0, 0.0);
        }
        Entity entity = this.getControllingPassenger();
        if (!this.isVehicle() || !(entity instanceof Player)) {
            this.maxUpStep = 0.5f;
            this.flyingSpeed = 0.02f;
            this.travelWithInput(vec3);
            return;
        }
        this.setYRot(entity.getYRot());
        this.yRotO = this.getYRot();
        this.setXRot(entity.getXRot() * 0.5f);
        this.setRot(this.getYRot(), this.getXRot());
        this.yBodyRot = this.getYRot();
        this.yHeadRot = this.getYRot();
        this.maxUpStep = 1.0f;
        this.flyingSpeed = this.getSpeed() * 0.1f;
        if (this.steering.boosting && this.steering.boostTime++ > 2) {
            this.steering.boosting = false;
        }
        float f = this.getSteeringSpeed();
        boolean flag = this.playerJumpPendingScale > 0.0F && !this.isCharging() && this.onGround;
        if (flag) {
            f += f * 1.15f * Mth.sin((float) steering.boostTime / (float) steering.boostTimeTotal * (float) Math.PI);
            this.setDeltaMovement(this.getDeltaMovement().add(this.getLookAngle().multiply(1.0, 0.0, 1.0).normalize().scale((double) (6.44444F) * this.getAttributeValue(Attributes.MOVEMENT_SPEED) * (double) this.getBlockSpeedFactor()).add(0.0, (double) (1.4285f * f) * 1.5F, 0.0)));
            this.chargingCooldown = 55;
            this.setCharging(true);
            this.hasImpulse = false;
            this.playerJumpPendingScale = 0.0F;
        }
        if (this.isControlledByLocalInstance()) {
            if (steering.boosting && steering.boostTime < 2) {
                f += f * 1.15f * Mth.sin((float) steering.boostTime / (float) steering.boostTimeTotal * (float) Math.PI);
            }
            this.setSpeed(f);
            this.travelWithInput(new Vec3(0.0, 0.0, 1.0));
            this.lerpSteps = 0;
        } else {
            if (this.onGround) {
                this.playerJumpPendingScale = 0.0F;
                this.setCharging(false);
            }
            this.calculateEntityAnimation(this, false);
            this.setDeltaMovement(Vec3.ZERO);
        }
        this.tryCheckInsideBlocks();

    }

    @Override
    public void tick() {
        super.tick();
        if (this.isCharging() && this.chargingCooldown < 55 && (this.onGround || this.isInWater())) {
            this.setCharging(false);
        }
        if (this.chargingCooldown > 0) {
            --this.chargingCooldown;
            if (this.chargingCooldown >= 49) {
                this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(2.0D), this::isValidTarget).forEach(this::damageRamTarget);
            }
            if (this.chargingCooldown == 0) {
                this.level.playSound(null, this.blockPosition(), BarnyardSounds.PIG_DASH_RECHARGE, SoundSource.PLAYERS, 1.0f, 1.0f);
            }
        }
    }

    private boolean isValidTarget(LivingEntity mob) {
        return mob.isAlive() && !mob.is(this) && !mob.is(getControllingPassenger());
    }

    private void damageRamTarget(LivingEntity mob) {
        if (getControllingPassenger() != null && getControllingPassenger() instanceof LivingEntity passenger) {
            Vec3 vec33 = mob.position().subtract(position().add(0.0, 1.6, 0.0)).normalize();
            double d = 0.25 * (1.0 - mob.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
            double e = (1.0 - mob.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
            mob.push(vec33.x() * e, vec33.y() * d, vec33.z() * e);
            DamageSource source = passenger instanceof Player player ? DamageSource.playerAttack(player) : DamageSource.mobAttack(passenger);
            mob.hurt(source, (float) getAttributeValue(Attributes.ATTACK_DAMAGE));
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
        InteractionResult interactionResult = super.mobInteract(player, hand);

        if (isSaddled()) {
            if (stack.is(ConventionalItemTags.SHEARS)) {
                steering.setSaddle(false);
                if (!player.getAbilities().instabuild) stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
                spawnAtLocation(Items.SADDLE);
                playSound(BarnyardSounds.PIG_SADDLE_UNEQUIP, 1, 1);
                return InteractionResult.sidedSuccess(level.isClientSide);
            } else if (isFood(stack)) {

            } else if (!isVehicle() && !player.isSecondaryUseActive()) {
                if (!level.isClientSide) player.startRiding(this);
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        } else {
            if (stack.is(Items.SADDLE)) return stack.interactLivingEntity(player, this, hand);
        }

        return interactionResult;
    }

    public boolean isFood(ItemStack itemStack) {
        return itemStack.is(BarnyardTags.PIG_BREEDS);
    }

    @Override
    public boolean canBeAffected(MobEffectInstance mobEffectInstance) {
        if (mobEffectInstance.getEffect() == MobEffects.POISON) return false;
        return super.canBeAffected(mobEffectInstance);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> entityDataAccessor) {
        if (!this.firstTick && CHARGING.equals(entityDataAccessor)) {
            this.chargingCooldown = this.chargingCooldown == 0 ? 55 : this.chargingCooldown;
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
        this.entityData.define(TUSK, false);
        this.entityData.define(DATA_SADDLE_ID, false);
        this.entityData.define(MUDDY, false);
        this.entityData.define(CHARGING, false);
        this.entityData.define(DATA_BOOST_TIME, 0);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putBoolean("Tusk", hasTusk());
        compoundTag.putBoolean("Muddy", isMuddy());
        steering.addAdditionalSaveData(compoundTag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        setHasTusk(compoundTag.getBoolean("Tusk"));
        setMuddy(compoundTag.getBoolean("Muddy"));
        steering.readAdditionalSaveData(compoundTag);
    }

    public boolean isCharging()  {
        return this.entityData.get(CHARGING);
    }

    public void setCharging(boolean charging) {
        this.entityData.set(CHARGING, charging);
    }

    public boolean isMuddy() {
        return entityData.get(MUDDY);
    }

    public void setMuddy(boolean muddy) {
        entityData.set(MUDDY, muddy);
    }

    public boolean hasTusk() {
        return entityData.get(TUSK);
    }

    public void setHasTusk(boolean tusk) {
        entityData.set(TUSK, tusk);
    }

    @Override
    public SpawnGroupData finalizeSpawn (ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, @Nullable SpawnGroupData spawnGroupData, @Nullable CompoundTag compoundTag){
        if (serverLevelAccessor.getRandom().nextFloat() < 0.2f) {
            setHasTusk(true);
        }
        return super.finalizeSpawn(serverLevelAccessor, difficultyInstance, mobSpawnType, spawnGroupData, compoundTag);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.2f)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5f)
                .add(Attributes.ATTACK_DAMAGE, 4.0);
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
    public AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob){
        return BarnyardEntityTypes.PIG.create(serverLevel);
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

    protected SoundEvent getStepSound() {
        return isMuddy() ? BarnyardSounds.PIG_STEP_MUDDY : BarnyardSounds.PIG_STEP;
    }

    protected void playStepSound(BlockPos blockPos, BlockState blockState){
        playSound(getStepSound(), 0.15F, 1.0F);
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
        return isAlive() && !isBaby() && hasTusk();
    }

    @Override
    public void equipSaddle(@Nullable SoundSource soundSource){
        steering.setSaddle(true);
        if (soundSource != null) {
            level.playSound(null, this, BarnyardSounds.PIG_SADDLE_EQUIP, soundSource, 0.5f, 1.0f);
        }
    }

    @Override
    public boolean isSaddled() {
        return steering.hasSaddle();
    }

    @Override
    public boolean boost() {
        playSound(BarnyardSounds.PIG_DASH);
        return steering.boost(getRandom());
    }

    @Override
    public void travelWithInput(Vec3 vec3){
        super.travel(vec3);
    }

   @Override
   public float getSteeringSpeed() {
       return (float) this.getAttributeValue(Attributes.MOVEMENT_SPEED) * 0.55f;
   }

    @Override
    public void onPlayerJump(int i) {
        if (!this.isSaddled() || this.chargingCooldown > 0 || !this.isOnGround()) {
            return;
        }
        if (i < 0) {
            i = 0;
        }
        this.playerJumpPendingScale = i >= 90 ? 1.0f : 0.4f + 0.4f * (float) i / 90.0f;
    }

    @Override
    public boolean canJump() {
        return this.isSaddled();
    }

    @Override
    public void handleStartJump(int i) {
        this.playSound(BarnyardSounds.PIG_DASH, 1.0F, 1.0F);
        this.setCharging(true);
    }

    @Override
    public void handleStopJump() {
    }

    @Override
    public int getJumpCooldown() {
        return this.chargingCooldown;
    }

}
