package com.ninni.barnyard.init;

import com.ninni.barnyard.Barnyard;
import com.ninni.barnyard.entities.BarnyardPig;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements.Type;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.levelgen.Heightmap;

public class BarnyardEntityTypes {

    public static final EntityType<BarnyardPig> PIG = register("pig", FabricEntityTypeBuilder.createMob()
            .entityFactory(BarnyardPig::new)
            .defaultAttributes(BarnyardPig::createAttributes)
            .spawnRestriction(Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules)
            .spawnGroup(MobCategory.CREATURE)
            .dimensions(EntityDimensions.scalable(0.9F, 0.9F)), null);

    @SuppressWarnings("unchecked")
    private static <T extends Entity> EntityType<T> register(String id, EntityType<T> entityType, int[] spawnEggColors) {
        if (spawnEggColors != null)
            Registry.register(Registry.ITEM, new ResourceLocation(Barnyard.MOD_ID, id + "_spawn_egg"), new SpawnEggItem((EntityType<? extends Mob>) entityType, spawnEggColors[0], spawnEggColors[1], new Item.Properties().stacksTo(64)));

        return Registry.register(Registry.ENTITY_TYPE, new ResourceLocation(Barnyard.MOD_ID, id), entityType);
    }

    private static <T extends Entity> EntityType<T> register(String id, FabricEntityTypeBuilder<T> entityType, int[] spawnEggColors) {
        return register(id, entityType.build(), spawnEggColors);
    }

}
