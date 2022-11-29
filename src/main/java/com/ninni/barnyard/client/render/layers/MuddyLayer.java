package com.ninni.barnyard.client.render.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.ninni.barnyard.client.model.BarnyardPigModel;
import com.ninni.barnyard.entities.BarnyardPig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

@Environment(value= EnvType.CLIENT)
public class MuddyLayer extends RenderLayer<BarnyardPig, BarnyardPigModel> {
    private final ResourceLocation textureLocation;
    private final BarnyardPigModel model;

    public MuddyLayer(RenderLayerParent<BarnyardPig, BarnyardPigModel> renderLayerParent, BarnyardPigModel entityModel, ResourceLocation resourceLocation) {
        super(renderLayerParent);
        this.model = entityModel;
        this.textureLocation = resourceLocation;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int i, BarnyardPig pig, float f, float g, float h, float j, float k, float l) {
        if (!pig.isMuddy()) return;

        float opacity;
        if (pig.getMuddyTicks() > 1800) opacity = 1;
        else opacity = pig.getMuddyTicks() * 0.00055555556F;

        (this.getParentModel()).copyPropertiesTo(this.model);
        (this.model).prepareMobModel(pig, f, g, h);
        (this.model).setupAnim(pig, f, g, j, k, l);
        VertexConsumer vertexConsumer = multiBufferSource.getBuffer(RenderType.entityTranslucent(this.textureLocation));
        (this.model).renderToBuffer(poseStack, vertexConsumer, i, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, opacity);
    }
}

