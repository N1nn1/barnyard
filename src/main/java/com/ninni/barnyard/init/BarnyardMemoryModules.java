package com.ninni.barnyard.init;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.ninni.barnyard.Barnyard;

import net.minecraft.core.Registry;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class BarnyardMemoryModules {
    
    public static final MemoryModuleType<Integer> PIG_SNIFFING_TICKS = register("pig_sniffing_ticks");

    protected static <U> MemoryModuleType<U> register(String string, Codec<U> codec) {
        return Registry.register(Registry.MEMORY_MODULE_TYPE, Barnyard.id(string), new MemoryModuleType<U>(Optional.of(codec)));
    }

    protected static <U> MemoryModuleType<U> register(String string) {
        return Registry.register(Registry.MEMORY_MODULE_TYPE, Barnyard.id(string), new MemoryModuleType<U>(Optional.empty()));
    }
}