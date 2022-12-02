package com.ninni.barnyard.init;

import net.minecraft.world.entity.Pose;

public enum BarnyardPose {
    RESTING;

    public Pose get() {
        return Pose.valueOf(this.name());
    }
}
