package com.ninni.barnyard.mixin.client;

import com.ninni.barnyard.entities.CooldownRideableJumping;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.PlayerRideableJumping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isRidingJumpable()Z"), method = "aiStep", cancellable = true)
    private void B$tick(CallbackInfo ci) {
        LocalPlayer $this = (LocalPlayer) (Object) this;
        if ($this instanceof CooldownRideableJumping cooldownRideableJumping && cooldownRideableJumping.getJumpCooldown() == 0) {
            ci.cancel();
        }
    }

}
