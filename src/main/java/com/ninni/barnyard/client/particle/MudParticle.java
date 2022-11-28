package com.ninni.barnyard.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

@Environment(EnvType.CLIENT)
public class MudParticle extends TextureSheetParticle {
    private final SpriteSet spriteSet;

    protected MudParticle(ClientLevel clientLevel, double d, double e, double f, SpriteSet spriteSet) {
        super(clientLevel, d, e, f);
        this.spriteSet = spriteSet;
        this.setLifetime(60);
        this.gravity = 0.5F;
        this.scale(1.25F);
        this.pickSprite(this.spriteSet);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Environment(value = EnvType.CLIENT)
    public record Factory(SpriteSet spriteSet) implements ParticleProvider<SimpleParticleType> {

        @Override
        public Particle createParticle(SimpleParticleType defaultParticleType, ClientLevel clientLevel, double d, double e, double f, double g, double h, double i) {
            return new MudParticle(clientLevel, d, e, f, this.spriteSet);
        }
    }
}
