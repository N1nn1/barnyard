package com.ninni.barnyard.mixin;

import com.ninni.barnyard.init.BarnyardParticleTypes;
import net.minecraft.world.entity.animal.Fox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Fox.class)
public class FoxMixin {
    int snoringTicks = 0;

    @Inject(at = @At("TAIL"), method = "aiStep")
    public void B$aiStep(CallbackInfo ci) {
        Fox $this = (Fox) (Object) this;

        if ($this.isSleeping()) {
            if (snoringTicks == 0) {
                this.snoringTicks = 30;
                $this.level.addParticle(BarnyardParticleTypes.SNORING, $this.getX(), $this.getY() + 0.5F, $this.getZ(), 0f, 0f, 0f);
            }
            if (snoringTicks > 0) this.snoringTicks--;
        }
    }
}
