package com.ninni.barnyard.client.render;

import com.ninni.barnyard.Barnyard;
import com.ninni.barnyard.client.model.BarnyardModelLayers;
import com.ninni.barnyard.client.model.BarnyardRabbitModel;
import com.ninni.barnyard.entities.BarnyardRabbit;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

@Environment(EnvType.CLIENT)
public class BarnyardRabbitRenderer extends MobRenderer<BarnyardRabbit, BarnyardRabbitModel> {

	private static final ResourceLocation RABBIT_BROWN_LOCATION = Barnyard.id("textures/entity/rabbit/brown.png");
	private static final ResourceLocation RABBIT_WHITE_LOCATION = Barnyard.id("textures/entity/rabbit/white.png");
	private static final ResourceLocation RABBIT_BLACK_LOCATION = Barnyard.id("textures/entity/rabbit/black.png");
	private static final ResourceLocation RABBIT_GOLD_LOCATION = Barnyard.id("textures/entity/rabbit/gold.png");
	private static final ResourceLocation RABBIT_SALT_LOCATION = Barnyard.id("textures/entity/rabbit/salt.png");
	private static final ResourceLocation RABBIT_WHITE_SPLOTCHED_LOCATION = Barnyard.id("textures/entity/rabbit/white_splotched.png");
	private static final ResourceLocation RABBIT_TOAST_LOCATION = Barnyard.id("textures/entity/rabbit/toast.png");
	private static final ResourceLocation RABBIT_EVIL_LOCATION = Barnyard.id("textures/entity/rabbit/caerbannog.png");

	public BarnyardRabbitRenderer(EntityRendererProvider.Context context) {
        super(context, new BarnyardRabbitModel(context.bakeLayer(BarnyardModelLayers.RABBIT)), 0.3f);
	}

	public ResourceLocation getTextureLocation(BarnyardRabbit rabbit) {
		String name = ChatFormatting.stripFormatting(rabbit.getName().getString());
		if (name != null && name == "Toast") {
			return RABBIT_TOAST_LOCATION;
		} else {
			switch (rabbit.getRabbitType()) {
				case 0:
				default:
					return RABBIT_BROWN_LOCATION;
				case 1:
					return RABBIT_WHITE_LOCATION;
				case 2:
					return RABBIT_BLACK_LOCATION;
				case 3:
					return RABBIT_WHITE_SPLOTCHED_LOCATION;
				case 4:
					return RABBIT_GOLD_LOCATION;
				case 5:
					return RABBIT_SALT_LOCATION;
				case 99:
					return RABBIT_EVIL_LOCATION;
			}
		}
	}
}