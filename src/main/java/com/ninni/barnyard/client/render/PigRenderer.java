package com.ninni.barnyard.client.render;

import com.ninni.barnyard.Barnyard;
import com.ninni.barnyard.client.model.BarnyardModelLayers;
import com.ninni.barnyard.client.model.PigModel;
import com.ninni.barnyard.entity.Pig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

@Environment(value= EnvType.CLIENT)
public class PigRenderer extends MobRenderer<Pig, PigModel> {
    private static final ResourceLocation PIG_LOCATION = new ResourceLocation(Barnyard.MOD_ID, "textures/entity/pig/pig.png");
    private static final ResourceLocation TUSKED_PIG_LOCATION = new ResourceLocation(Barnyard.MOD_ID, "textures/entity/pig/tusked_pig.png");

    public PigRenderer(EntityRendererProvider.Context context) {
        super(context, new PigModel(context.bakeLayer(BarnyardModelLayers.PIG)), 0.7f);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(Pig entity) {
        return PIG_LOCATION;
    }
}

