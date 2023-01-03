package com.ninni.barnyard.init;

import com.ninni.barnyard.Barnyard;
import com.ninni.barnyard.entities.BarnyardPig;
import com.ninni.barnyard.entities.BarnyardRabbit;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements.Type;
import net.minecraft.world.level.levelgen.Heightmap;

public class BarnyardEntityTypes {

    public static final EntityType<BarnyardPig> PIG = register("pig", FabricEntityTypeBuilder.createMob()
            .entityFactory(BarnyardPig::new)
            .defaultAttributes(BarnyardPig::createAttributes)
            .spawnRestriction(Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, BarnyardPig::checkSpawnRules)
            .spawnGroup(MobCategory.AMBIENT)
            .dimensions(EntityDimensions.scalable(0.9F, 0.9F)));

     public static final EntityType<BarnyardRabbit> RABBIT = register("rabbit", FabricEntityTypeBuilder.createMob()
            .entityFactory(BarnyardRabbit::new)
            .defaultAttributes(BarnyardRabbit::createAttributes)
            .spawnRestriction(Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, BarnyardRabbit::checkSpawnRules)
            .spawnGroup(MobCategory.AMBIENT)
            .dimensions(EntityDimensions.scalable(0.4F, 0.5F)));

    private static <T extends Entity> EntityType<T> register(String id, EntityType<T> entityType) {
        return Registry.register(Registry.ENTITY_TYPE, new ResourceLocation(Barnyard.MOD_ID, id), entityType);
    }

    private static <T extends Entity> EntityType<T> register(String id, FabricEntityTypeBuilder<T> entityType) {
        return register(id, entityType.build());
    }
}
