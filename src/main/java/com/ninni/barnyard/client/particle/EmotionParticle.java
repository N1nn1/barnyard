package com.ninni.barnyard.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

@Environment(EnvType.CLIENT)
public class EmotionParticle extends TextureSheetParticle {
    private final SpriteSet spriteSet;

    protected EmotionParticle(ClientLevel clientLevel, double d, double e, double f, SpriteSet spriteSet) {
        super(clientLevel, d, e, f);
        this.spriteSet = spriteSet;
        this.setLifetime(60);
        this.gravity = -0.005F;
        this.quadSize = 0.4F;
        this.setSpriteFromAge(spriteSet);
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteFromAge(spriteSet);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Environment(value = EnvType.CLIENT)
    public record Factory(SpriteSet spriteSet) implements ParticleProvider<SimpleParticleType> {

        @Override
        public Particle createParticle(SimpleParticleType defaultParticleType, ClientLevel clientLevel, double d, double e, double f, double g, double h, double i) {
            return new EmotionParticle(clientLevel, d, e, f, this.spriteSet);
        }
    }
}
