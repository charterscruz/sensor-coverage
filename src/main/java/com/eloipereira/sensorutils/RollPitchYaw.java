package com.eloipereira.sensorutils;

/**
 * Created by eloi on 7/23/14.
 */

public class RollPitchYaw extends Attitude {
    Double roll; // roll angle in degrees
    Double pitch; // pitch angle in degrees
    Double yaw; // yaw angle in degrees

    public RollPitchYaw(Double roll, Double pitch, Double yaw) {
        this.roll = roll;
        this.pitch = pitch;
        this.yaw = yaw;
    }
}
