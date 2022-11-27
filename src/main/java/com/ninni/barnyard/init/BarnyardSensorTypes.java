package com.ninni.barnyard.init;

import com.ninni.barnyard.Barnyard;
import com.ninni.barnyard.entities.ai.BarnyardPigAi;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.ai.sensing.TemptingSensor;

import java.util.function.Supplier;

public class BarnyardSensorTypes {

    public static final SensorType<TemptingSensor> PIG_TEMPTATIONS = register("pig_temptations", () -> new TemptingSensor(BarnyardPigAi.getTemptations()));

    private static <U extends Sensor<?>> SensorType<U> register(String string, Supplier<U> supplier) {
        return Registry.register(Registry.SENSOR_TYPE, new ResourceLocation(Barnyard.MOD_ID, string), new SensorType<U>(supplier));
    }

}
