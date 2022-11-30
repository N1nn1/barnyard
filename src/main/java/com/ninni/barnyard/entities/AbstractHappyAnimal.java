package com.ninni.barnyard.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public abstract class AbstractHappyAnimal extends Animal {
    private static final EntityDataAccessor<Integer> HAPPINESS_LEVEL = SynchedEntityData.defineId(AbstractHappyAnimal.class, EntityDataSerializers.INT);
    private static final UUID SAD_MOVEMENT_MODIFIER_UUID = UUID.fromString("ad287975-57a5-45fe-b0b4-ecbfe068b766");

    protected AbstractHappyAnimal(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(HAPPINESS_LEVEL, 0);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.setHappinessLevel(compoundTag.getInt("HappinessLevel"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putInt("HappinessLevel", this.getHappinessLevel());
    }

    @Override
    protected void usePlayerItem(Player player, InteractionHand interactionHand, ItemStack itemStack) {
        if (this.isFood(itemStack)) {
            this.increaseHappyLevel();
        }
        super.usePlayerItem(player, interactionHand, itemStack);
    }

    public void setHappinessLevel(int happinessLevel) {
        this.entityData.set(HAPPINESS_LEVEL, happinessLevel);
    }

    public int getHappinessLevel() {
        return this.entityData.get(HAPPINESS_LEVEL);
    }

    public void increaseHappyLevel() {
        this.setHappinessLevel(Math.max(this.getMaxHappyLevel(), this.getHappinessLevel() + 1));
    }

    public void decreaseHappyLevel() {
        this.setHappinessLevel(Math.max(this.getMinHappyLevel(), this.getHappinessLevel() - 1));
    }

    @Override
    public boolean hurt(DamageSource damageSource, float f) {
        this.decreaseHappyLevel();
        return super.hurt(damageSource, f);
    }

    @Override
    public void tick() {
        super.tick();
        Vec3 angle = this.getLookAngle().yRot(-this.yBodyRot * ((float) Math.PI) / 180).scale(0.5D).add(this.getX(), this.getEyeY(), this.getZ());
        boolean happy = this.isHappy();
        double randomX = happy ? this.getRandomX(1.0D) : angle.x;
        double randomY = happy ? this.getRandomY() + 0.5D : angle.y;
        double randomZ = happy ? this.getRandomZ(1.0D) : angle.z;
        float threshold = happy ? 0.05F : 0.55F;
        SimpleParticleType particle = happy ? ParticleTypes.HEART : ParticleTypes.SPLASH;
        if (this.random.nextFloat() < threshold) {
            int count = Math.max(Math.abs(this.getHappinessLevel()), 1);
            for (int i = 0; i < this.random.nextInt(count); ++i) {
                double d = this.random.nextGaussian() * 0.02;
                double e = this.random.nextGaussian() * 0.02;
                double f = this.random.nextGaussian() * 0.02;
                this.level.addParticle(particle, randomX, randomY, randomZ, d, e, f);
            }
        }
    }

    private boolean isHappy() {
        return this.getHappinessLevel() >= 0;
    }

    private void removeSadMovementModifier() {
        AttributeInstance attributeInstance = this.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attributeInstance == null) {
            return;
        }
        if (attributeInstance.getModifier(SAD_MOVEMENT_MODIFIER_UUID) != null) {
            attributeInstance.removeModifier(SAD_MOVEMENT_MODIFIER_UUID);
        }
    }

    private void addSadMovementModifier() {
        AttributeInstance attributeInstance = this.getAttribute(Attributes.MOVEMENT_SPEED);
        float speedModifier = this.getHappinessLevel() * 0.01F;
        AttributeModifier modifier = new AttributeModifier(SAD_MOVEMENT_MODIFIER_UUID, "Sad movement modifier", speedModifier, AttributeModifier.Operation.ADDITION);
        if (attributeInstance == null || attributeInstance.hasModifier(modifier)) {
            return;
        }
        attributeInstance.addTransientModifier(modifier);
    }

    @Override
    protected void onChangedBlock(BlockPos blockPos) {
        super.onChangedBlock(blockPos);
        if (this.getHappinessLevel() > 3) {
            this.removeSadMovementModifier();
        }
        this.addSadMovementModifier();
    }

    public abstract int getMinHappyLevel();

    public abstract int getMaxHappyLevel();

}
