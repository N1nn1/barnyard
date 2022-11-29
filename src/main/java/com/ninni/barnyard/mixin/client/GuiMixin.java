package com.ninni.barnyard.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ninni.barnyard.entities.BarnyardPig;
import com.ninni.barnyard.entities.CooldownRideableJumping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiMixin {

    @Shadow @Final private Minecraft minecraft;

    @Shadow private int screenHeight;

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;blit(Lcom/mojang/blaze3d/vertex/PoseStack;IIIIII)V", shift = At.Shift.AFTER, ordinal = 0), method = "renderJumpMeter", cancellable = true)
    private void B$renderJumpMeter(PoseStack poseStack, int i, CallbackInfo ci) {
        LocalPlayer player = this.minecraft.player;
        int l = this.screenHeight - 32 + 3;
        if (player.getVehicle() instanceof CooldownRideableJumping vehicle && vehicle.getJumpCooldown() > 0) {
            ci.cancel();
            ((Gui)(Object)this).blit(poseStack, i, l, 0, 74, 182, 5);
        }

        if (player.getVehicle() instanceof BarnyardPig pig && !pig.hasTusk()) {
            ci.cancel();
        }
    }

}
