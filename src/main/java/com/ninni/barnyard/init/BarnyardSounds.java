package com.ninni.barnyard.init;

import com.ninni.barnyard.Barnyard;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.SoundType;

public interface BarnyardSounds {

    public static final SoundEvent PIG_AMBIENT = register("enity.pig.ambient");
    public static final SoundEvent PIG_DASH = register("entity.pig.dash");
    public static final SoundEvent PIG_DASH_RECHARGE = register("entity.pig.dash.recharge");
    public static final SoundEvent PIG_DASH_RAM = register("entity.pig.dash.ram");
    public static final SoundEvent PIG_DEATH = register("entity.pig.death");
    public static final SoundEvent PIG_HURT = register("entity.pig.hurt");
    public static final SoundEvent PIG_STEP = register("entity.pig.step");
    public static final SoundEvent PIG_SNIFF = register("entity.pig.sniff");
    public static final SoundEvent PIG_SADDLE_EQUIP = register("entity.pig.saddle_equip");
    public static final SoundEvent PIG_SADDLE_UNEQUIP = register("entity.pig.saddle_unequip");

    SoundType THATCH = register("thatch", 2F, 0.75F);

    private static SoundType register(String name, float volume, float pitch) {
        return new SoundType(volume, pitch, register("block." + name + ".break"), register("block." + name + ".step"), register("block." + name + ".place"), register("block." + name + ".hit"), register("block." + name + ".fall"));
    }

    static SoundEvent register(String id) {
        ResourceLocation rl = new ResourceLocation(Barnyard.MOD_ID, id);
        return Registry.register(Registry.SOUND_EVENT, rl, new SoundEvent(rl));
    }
}
