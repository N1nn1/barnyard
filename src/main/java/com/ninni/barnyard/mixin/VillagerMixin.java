package com.ninni.barnyard.mixin;

import com.ninni.barnyard.init.BarnyardParticleTypes;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.npc.Villager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Villager.class)
public class VillagerMixin {
    int snoringTicks = 0;

    @Inject(at = @At("TAIL"), method = "tick")
    public void B$tick(CallbackInfo ci) {
        Villager $this = (Villager) (Object) this;

        if ($this.getPose() == Pose.SLEEPING) {
            if (snoringTicks == 0) {
                this.snoringTicks = 30;
                $this.level.addParticle(BarnyardParticleTypes.SNORING, $this.getX(), $this.getY() + 0.75F, $this.getZ(), 0f, 0f, 0f);
            }
            if (snoringTicks > 0) this.snoringTicks--;
        }
    }
}
