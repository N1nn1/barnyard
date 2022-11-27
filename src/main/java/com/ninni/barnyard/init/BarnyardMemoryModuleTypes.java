package com.ninni.barnyard.init;

import com.mojang.serialization.Codec;
import com.ninni.barnyard.Barnyard;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

import java.util.Optional;

public class BarnyardMemoryModuleTypes {

    public static final MemoryModuleType<Integer> SNIFFING_COOLDOWN_TICKS = register("sniffing_cooldown_ticks", Codec.INT);

    private static <U> MemoryModuleType<U> register(String string, Codec<U> codec) {
        return Registry.register(Registry.MEMORY_MODULE_TYPE, new ResourceLocation(Barnyard.MOD_ID, string), new MemoryModuleType<U>(Optional.of(codec)));
    }

}
