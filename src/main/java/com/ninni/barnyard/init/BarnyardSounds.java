package com.ninni.barnyard.init;

import com.ninni.barnyard.Barnyard;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.SoundType;

public interface BarnyardSounds {

    SoundType THATCH = register("thatch", 2F, 0.75F);

    private static SoundType register(String name, float volume, float pitch) {
        return new SoundType(volume, pitch, register("block." + name + ".break"), register("block." + name + ".step"), register("block." + name + ".place"), register("block." + name + ".hit"), register("block." + name + ".fall"));
    }

    static SoundEvent register(String id) {
        ResourceLocation rl = new ResourceLocation(Barnyard.MOD_ID, id);
        return Registry.register(Registry.SOUND_EVENT, rl, new SoundEvent(rl));
    }
}
