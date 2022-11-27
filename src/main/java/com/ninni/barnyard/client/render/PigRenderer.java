package com.ninni.barnyard.client.render;

import com.ninni.barnyard.Barnyard;
import com.ninni.barnyard.client.model.BarnyardModelLayers;
import com.ninni.barnyard.client.model.PigModel;
import com.ninni.barnyard.entities.BarnyardPig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

@Environment(value=EnvType.CLIENT)
public class PigRenderer extends MobRenderer<BarnyardPig, PigModel> {
    private static final ResourceLocation PIG_LOCATION = new ResourceLocation(Barnyard.MOD_ID, "textures/entity/pig/pig.png");
    private static final ResourceLocation TUSKED_PIG_LOCATION = new ResourceLocation(Barnyard.MOD_ID, "textures/entity/pig/tusked_pig.png");

    public PigRenderer(EntityRendererProvider.Context context) {
        super(context, new PigModel(context.bakeLayer(BarnyardModelLayers.PIG)), 0.7f);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(BarnyardPig entity) {
        return entity.hasTusk() ? TUSKED_PIG_LOCATION : PIG_LOCATION;
    }
}

