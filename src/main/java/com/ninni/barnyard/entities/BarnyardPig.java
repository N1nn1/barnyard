package com.ninni.barnyard.entities;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import com.ninni.barnyard.entities.ai.BarnyardPigAi;
import com.ninni.barnyard.init.BarnyardEntityTypes;
import com.ninni.barnyard.init.BarnyardSensorTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ItemBasedSteering;
import net.minecraft.world.entity.ItemSteerable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
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
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class BarnyardPig extends Animal implements Saddleable, ItemSteerable {
    protected static final ImmutableList<SensorType<? extends Sensor<? super BarnyardPig>>> SENSOR_TYPES = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.NEAREST_ITEMS, SensorType.NEAREST_ADULT, SensorType.HURT_BY, BarnyardSensorTypes.PIG_TEMPTATIONS);
    protected static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(MemoryModuleType.LOOK_TARGET, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryModuleType.WALK_TARGET, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.PATH, MemoryModuleType.ATE_RECENTLY, MemoryModuleType.BREED_TARGET, MemoryModuleType.TEMPTING_PLAYER, MemoryModuleType.NEAREST_VISIBLE_ADULT, MemoryModuleType.TEMPTATION_COOLDOWN_TICKS, MemoryModuleType.IS_TEMPTED, MemoryModuleType.IS_PANICKING, MemoryModuleType.IS_SNIFFING, MemoryModuleType.SNIFF_COOLDOWN);
    private static final EntityDataAccessor<Boolean> TUSK = SynchedEntityData.defineId(BarnyardPig.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_SADDLE_ID = SynchedEntityData.defineId(BarnyardPig.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_BOOST_TIME = SynchedEntityData.defineId(BarnyardPig.class, EntityDataSerializers.INT);
    private final ItemBasedSteering steering;

    public BarnyardPig(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
        this.steering = new ItemBasedSteering(this.entityData, DATA_BOOST_TIME, DATA_SADDLE_ID);
    }

    @Override
    public void travel(Vec3 vec3) {
        if (!this.isAlive()) {
            return;
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
        if (steering.boosting && steering.boostTime++ > 2) {
            steering.boosting = false;
        }
        if (steering.boostTime <= 3) {
            this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(2.0D), this::isValidTarget).forEach(this::damageRamTarget);
        }
        if (this.isControlledByLocalInstance()) {
            float f = this.getSteeringSpeed();
            if (steering.boosting && steering.boostTime < 2) {
                f += f * 1.15f * Mth.sin((float)steering.boostTime / (float)steering.boostTimeTotal * (float)Math.PI);
                this.setDeltaMovement(this.getDeltaMovement().add(this.getLookAngle().multiply(1.0, 0.0, 1.0).normalize().scale((double)(4.44444F) * this.getAttributeValue(Attributes.MOVEMENT_SPEED) * (double)this.getBlockSpeedFactor()).add(0.0, (double)(1.4285f * f) * 1.5F, 0.0)));
            }
            this.setSpeed(f);
            this.travelWithInput(new Vec3(0.0, 0.0, 1.0));
            this.lerpSteps = 0;
        } else {
            this.calculateEntityAnimation(this, false);
            this.setDeltaMovement(Vec3.ZERO);
        }
        this.tryCheckInsideBlocks();
    }

    private boolean isValidTarget(LivingEntity livingEntity) {
        return livingEntity.isAlive() && !livingEntity.is(this) && !livingEntity.is(this.getControllingPassenger());
    }

    private void damageRamTarget(LivingEntity livingEntity) {
        Vec3 vec33 = livingEntity.position().subtract(this.position().add(0.0, 1.6, 0.0)).normalize();
        double d = 0.5 * (1.0 - livingEntity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
        double e = 2.5 * (1.0 - livingEntity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
        livingEntity.push(vec33.x() * e, vec33.y() * d, vec33.z() * e);
        livingEntity.hurt(DamageSource.mobAttack(this).setNoAggro(), (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE));
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand interactionHand) {
        boolean bl = this.isFood(player.getItemInHand(interactionHand));
        if (!bl && this.isSaddled() && !this.isVehicle() && !player.isSecondaryUseActive()) {
            if (!this.level.isClientSide) {
                player.startRiding(this);
            }
            return InteractionResult.sidedSuccess(this.level.isClientSide);
        }
        InteractionResult interactionResult = super.mobInteract(player, interactionHand);
        if (!interactionResult.consumesAction()) {
            ItemStack itemStack = player.getItemInHand(interactionHand);
            if (itemStack.is(Items.SADDLE)) {
                return itemStack.interactLivingEntity(player, this, interactionHand);
            }
            return InteractionResult.PASS;
        }
        return interactionResult;
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> entityDataAccessor) {
        if (DATA_BOOST_TIME.equals(entityDataAccessor) && this.level.isClientSide) {
            this.steering.onSynced();
        }
        super.onSyncedDataUpdated(entityDataAccessor);
    }

    @Nullable
    @Override
    public Entity getControllingPassenger() {
        Entity entity = this.getFirstPassenger();
        return entity != null && this.canBeControlledBy(entity) ? entity : null;
    }

    private boolean canBeControlledBy(Entity entity) {
        if (this.isSaddled() && entity instanceof Player player) {
            return player.getMainHandItem().is(Items.CARROT_ON_A_STICK) || player.getOffhandItem().is(Items.CARROT_ON_A_STICK);
        }
        return false;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(TUSK, false);
        this.entityData.define(DATA_SADDLE_ID, false);
        this.entityData.define(DATA_BOOST_TIME, 0);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putBoolean("Tusk", this.hasTusk());
        this.steering.addAdditionalSaveData(compoundTag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.setHasTusk(compoundTag.getBoolean("Tusk"));
        this.steering.readAdditionalSaveData(compoundTag);
    }

    public boolean hasTusk() {
        return this.entityData.get(TUSK);
    }

    public void setHasTusk(boolean tusk) {
        this.entityData.set(TUSK, tusk);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, @Nullable SpawnGroupData spawnGroupData, @Nullable CompoundTag compoundTag) {
        if (serverLevelAccessor.getRandom().nextFloat() < 0.2f) {
            this.setHasTusk(true);
        }
        return super.finalizeSpawn(serverLevelAccessor, difficultyInstance, mobSpawnType, spawnGroupData, compoundTag);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 10.0).add(Attributes.MOVEMENT_SPEED, 0.2f).add(Attributes.ATTACK_DAMAGE, 4.0);
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

    @Override
    public boolean isSaddleable() {
        return this.isAlive() && !this.isBaby() && this.hasTusk();
    }

    @Override
    public void equipSaddle(@Nullable SoundSource soundSource) {
        this.steering.setSaddle(true);
        if (soundSource != null) {
            this.level.playSound(null, this, SoundEvents.PIG_SADDLE, soundSource, 0.5f, 1.0f);
        }
    }

    @Override
    public boolean isSaddled() {
        return this.steering.hasSaddle();
    }

    @Override
    public boolean boost() {
        return this.steering.boost(this.getRandom());
    }

    @Override
    public void travelWithInput(Vec3 vec3) {
        super.travel(vec3);
    }

    @Override
    public float getSteeringSpeed() {
        return (float)this.getAttributeValue(Attributes.MOVEMENT_SPEED) * 0.225f;
    }

}
