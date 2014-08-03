package com.eloipereira.sensorutils;

/**
 * Created by eloi on 7/23/14.
 */
public class PanTilt extends Attitude {
    Double pan;
    Double tilt;

    public PanTilt(Double pan, Double tilt) {
        this.pan = pan;
        this.tilt = tilt;
    }
}
