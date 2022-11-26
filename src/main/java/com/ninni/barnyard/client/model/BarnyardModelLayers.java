package com.ninni.barnyard.client.model;

import com.ninni.barnyard.Barnyard;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;

public interface BarnyardModelLayers {

   ModelLayerLocation PIG = create("pig", "main");

    private static ModelLayerLocation create(String id, String layer) {
        return new ModelLayerLocation(new ResourceLocation(Barnyard.MOD_ID, id), layer);
    }

}
