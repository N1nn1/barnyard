package com.ninni.barnyard.client.render;

import com.ninni.barnyard.Barnyard;
import com.ninni.barnyard.client.model.BarnyardModelLayers;
import com.ninni.barnyard.client.model.BarnyardPigModel;
import com.ninni.barnyard.entities.BarnyardPig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.SaddleLayer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

@Environment(value=EnvType.CLIENT)
public class BarnyardPigRenderer extends MobRenderer<BarnyardPig, BarnyardPigModel> {
    private static final ResourceLocation PIG_LOCATION = new ResourceLocation(Barnyard.MOD_ID, "textures/entity/pig/pig.png");
    private static final ResourceLocation TUSKED_PIG_LOCATION = new ResourceLocation(Barnyard.MOD_ID, "textures/entity/pig/tusked_pig.png");
    private static final ResourceLocation MUDDY_PIG_LOCATION = new ResourceLocation(Barnyard.MOD_ID, "textures/entity/pig/muddy_pig.png");
    private static final ResourceLocation MUDDY_TUSKED_PIG_LOCATION = new ResourceLocation(Barnyard.MOD_ID, "textures/entity/pig/muddy_tusked_pig.png");
    private static final ResourceLocation SADDLE_LOCATION = new ResourceLocation(Barnyard.MOD_ID, "textures/entity/pig/pig_saddle.png");

    public BarnyardPigRenderer(EntityRendererProvider.Context context) {
        super(context, new BarnyardPigModel(context.bakeLayer(BarnyardModelLayers.PIG)), 0.6f);
        this.addLayer(new SaddleLayer<>(this, new BarnyardPigModel(context.bakeLayer(BarnyardModelLayers.PIG_SADDLE)), SADDLE_LOCATION));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(BarnyardPig entity) {
        if (entity.isMuddy()) {
            return entity.hasTusk() ? MUDDY_TUSKED_PIG_LOCATION : MUDDY_PIG_LOCATION;
        }
        return entity.hasTusk() ? TUSKED_PIG_LOCATION : PIG_LOCATION;
    }
}

