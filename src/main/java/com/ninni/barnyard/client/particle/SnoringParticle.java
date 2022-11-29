package com.ninni.barnyard.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;

@Environment(EnvType.CLIENT)
public class SnoringParticle extends TextureSheetParticle {
    private final SpriteSet spriteSet;


    SnoringParticle(ClientLevel level, double x, double y, double z, SpriteSet spriteSet) {
        super(level, x, y, z);
        this.spriteSet = spriteSet;
        gravity = -0.025F;
        this.lifetime = 60;
        this.setSpriteFromAge(spriteSet);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void tick() {
        super.tick();
        if (age < lifetime) {
            if (alpha > 0.1F) {
                alpha -= 0.015F;
            } else {
                this.remove();
            }
        }
        xd = Mth.sin(age * 0.125F) * 0.05F;
        this.setSpriteFromAge(spriteSet);
    }

    @Override
    @SuppressWarnings("deprecation")
    protected int getLightColor(float f) {
        BlockPos blockPos = new BlockPos(this.x, this.y, this.z);
        if (this.level.hasChunkAt(blockPos)) {
            return LevelRenderer.getLightColor(this.level, blockPos);
        }
        return 0;
    }

    @Environment(value = EnvType.CLIENT)
    public record Factory(SpriteSet spriteSet) implements ParticleProvider<SimpleParticleType> {

        @Override
        public Particle createParticle(SimpleParticleType defaultParticleType, ClientLevel level, double d, double e, double f, double g, double h, double i) {
            return new SnoringParticle(level, d, e, f, this.spriteSet);
        }
    }
}
