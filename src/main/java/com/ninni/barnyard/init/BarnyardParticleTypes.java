package com.ninni.barnyard.init;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;

import static com.ninni.barnyard.Barnyard.MOD_ID;

public class BarnyardParticleTypes {
    public static final SimpleParticleType MUD = Registry.register(Registry.PARTICLE_TYPE, new ResourceLocation(MOD_ID, "mud"), FabricParticleTypes.simple());
}
