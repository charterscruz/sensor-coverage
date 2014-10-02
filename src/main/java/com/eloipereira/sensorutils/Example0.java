package com.eloipereira.sensorutils;

import com.eloipereira.geoutils.Coordinate;
import de.micromata.opengis.kml.v_2_2_0.Kml;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by eloi on 7/24/14.
 */
public class Example0 {

    public static void main(String[] args) {
        Coordinate uavGeoLoc = new Coordinate(39.232,-9.755,200);
        RollPitchYaw uavAtt = new RollPitchYaw(30.0,-10.0,0.0);
        PanTilt cameraAtt = new PanTilt(0.0,-90.0);
        int focal = 500;
        Coordinate sensorOffset = new Coordinate(0.0,0.0,0.0);
        int sensorWidth = 640;
        int sensorHeight = 480;

        Polygon poly = SensorCoverage.getGeoSensorFootprint(uavGeoLoc,uavAtt,sensorOffset,cameraAtt,sensorWidth,sensorHeight,focal);
        Kml kml = poly.toKml();
        kml.marshal();
//        try {
//            kml.marshal(new File("src/main/resources/footprint.kml"));
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
    }
}
