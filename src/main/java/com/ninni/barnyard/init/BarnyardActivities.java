package com.ninni.barnyard.init;

import com.ninni.barnyard.Barnyard;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.schedule.Activity;

public class BarnyardActivities {

    public static final Activity MUD_ROLLING = register("mud_rolling");

    private static Activity register(String string) {
        return Registry.register(Registry.ACTIVITY, new ResourceLocation(Barnyard.MOD_ID, string), new Activity(string));
    }

}
