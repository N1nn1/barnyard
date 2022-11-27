package com.ninni.barnyard.init;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import static com.ninni.barnyard.Barnyard.MOD_ID;

public interface BarnyardTags {
    TagKey<Item> PIG_TEMPTS = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(MOD_ID, "pig_tempts"));
    TagKey<Item> PIG_BREEDS = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(MOD_ID, "pig_breeds"));
}
