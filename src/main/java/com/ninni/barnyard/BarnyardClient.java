package com.ninni.barnyard;

import com.ninni.barnyard.client.model.BarnyardModelLayers;
import com.ninni.barnyard.client.model.PigModel;
import com.ninni.barnyard.client.render.PigRenderer;
import com.ninni.barnyard.init.BarnyardBlocks;
import com.ninni.barnyard.init.BarnyardEntityTypes;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.RenderType;

@Environment(EnvType.CLIENT)
public class BarnyardClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderType.cutout(),
                BarnyardBlocks.THATCH
        );
        EntityRendererRegistry.register(BarnyardEntityTypes.PIG, PigRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(BarnyardModelLayers.PIG, PigModel::getLayerDefinition);
    }
}
