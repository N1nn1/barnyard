package com.ninni.barnyard.init;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;

import static com.ninni.barnyard.Barnyard.MOD_ID;

public class BarnyardParticleTypes {
    public static final SimpleParticleType MUD = Registry.register(Registry.PARTICLE_TYPE, new ResourceLocation(MOD_ID, "mud"), FabricParticleTypes.simple());
    public static final SimpleParticleType SNORING = Registry.register(Registry.PARTICLE_TYPE, new ResourceLocation(MOD_ID, "snoring"), FabricParticleTypes.simple());
    public static final SimpleParticleType EMOTION_SAD = Registry.register(Registry.PARTICLE_TYPE, new ResourceLocation(MOD_ID, "emotion_sad"), FabricParticleTypes.simple());
    public static final SimpleParticleType EMOTION_NEUTRAL = Registry.register(Registry.PARTICLE_TYPE, new ResourceLocation(MOD_ID, "emotion_neutral"), FabricParticleTypes.simple());
    public static final SimpleParticleType EMOTION_HAPPY = Registry.register(Registry.PARTICLE_TYPE, new ResourceLocation(MOD_ID, "emotion_happy"), FabricParticleTypes.simple());
    public static final SimpleParticleType EMOTION_JOYOUS = Registry.register(Registry.PARTICLE_TYPE, new ResourceLocation(MOD_ID, "emotion_joyous"), FabricParticleTypes.simple());
}
