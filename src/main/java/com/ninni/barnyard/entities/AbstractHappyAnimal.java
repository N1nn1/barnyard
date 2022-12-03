package com.ninni.barnyard.entities;

import com.ninni.barnyard.init.BarnyardParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.UUID;

public abstract class AbstractHappyAnimal extends Animal {
    private static final EntityDataAccessor<Integer> HAPPINESS_LEVEL = SynchedEntityData.defineId(AbstractHappyAnimal.class, EntityDataSerializers.INT);
    private static final UUID SAD_MOVEMENT_MODIFIER_UUID = UUID.fromString("ad287975-57a5-45fe-b0b4-ecbfe068b766");
    private int petCooldown = 40;

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
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (petCooldown == 0 && player.getItemInHand(hand).isEmpty() && player.isShiftKeyDown() && !this.isSleeping()) {
            petCooldown = 40;
            this.level.addParticle(getEmotionParticle(), this.getX(), this.getY() + 1.25F, this.getZ(), 0, 0, 0);
            this.playAmbientSound();
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    public SimpleParticleType getEmotionParticle() {
        int happiness = this.getHappinessLevel();

        if (happiness == getMaxHappyLevel()) return BarnyardParticleTypes.EMOTION_JOYOUS;
        if (happiness > 0) return BarnyardParticleTypes.EMOTION_HAPPY;
        if (happiness != getMinHappyLevel()) return BarnyardParticleTypes.EMOTION_NEUTRAL;
        else return BarnyardParticleTypes.EMOTION_SAD;
    }

    @Override
    public float getVoicePitch() {
        if (this.isBaby()) return (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.5f;
        else {
            int happiness = this.getHappinessLevel();
            if (happiness == getMaxHappyLevel()) return (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.25f;
            if (happiness > 0) return (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f;
            if (happiness != getMinHappyLevel()) return (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 0.85f;
        }
        return (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 0.75f;
    }

    @Override
    public void tick() {
        super.tick();
        if (petCooldown > 0) petCooldown--;
    }

    private void removeSadMovementModifier() {
        AttributeInstance attributeInstance = this.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attributeInstance == null) return;
        if (attributeInstance.getModifier(SAD_MOVEMENT_MODIFIER_UUID) != null) {
            attributeInstance.removeModifier(SAD_MOVEMENT_MODIFIER_UUID);
        }
    }

    private void addSadMovementModifier() {
        AttributeInstance attributeInstance = this.getAttribute(Attributes.MOVEMENT_SPEED);
        float speedModifier = this.getHappinessLevel() * 0.01F;
        AttributeModifier modifier;
        if (this.getHappinessLevel() < 0) modifier = new AttributeModifier(SAD_MOVEMENT_MODIFIER_UUID, "Sad movement modifier", speedModifier, AttributeModifier.Operation.ADDITION);
        else modifier = new AttributeModifier(SAD_MOVEMENT_MODIFIER_UUID, "Sad movement modifier", 0, AttributeModifier.Operation.ADDITION);
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
