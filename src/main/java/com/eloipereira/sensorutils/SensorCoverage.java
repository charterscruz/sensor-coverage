package com.eloipereira.sensorutils;

import Jama.Matrix;
import com.eloipereira.geoutils.Coordinate;
import com.eloipereira.geoutils.GeoUtils;
import de.micromata.opengis.kml.v_2_2_0.Boundary;
import de.micromata.opengis.kml.v_2_2_0.Kml;
import de.micromata.opengis.kml.v_2_2_0.LinearRing;

/**
 * Created by eloi on 7/24/14.
 */
public class SensorCoverage {

    private static void printMatrix(Matrix m){
        String s = "";
        for(int i =0; i<m.getRowDimension() ; i++ ){
            s += "\n[ ";
            for(int j=0;j<m.getColumnDimension();j++){
                s += m.get(i,j) + " ";
            }
            s += "]";
        }
        System.out.println(s);
    }


    public static Coordinate getPixelCoordinates(Coordinate vehicleGeoLoc,
                                                 RollPitchYaw vehicleAtt,
                                                 Coordinate sensorOffset,
                                                 PanTilt sensorAtt,
                                                 int sensorFocal,
                                                 Coordinate imageCoord){

        double [][] arrayImage2Mount = {
                {0,0,1},
                {1,0,0},
                {0,1,0}
        }; //rotate image frame to mount/gimbal frame

        Matrix image2mount = new Matrix(arrayImage2Mount);

        double cosTau = Math.cos(Math.toRadians(sensorAtt.tilt));
        double cosRo = Math.cos(Math.toRadians(sensorAtt.pan));
        double sinRo = Math.sin(Math.toRadians(sensorAtt.pan));
        double sinTau = Math.sin(Math.toRadians(sensorAtt.tilt));

        double [][] arrayBody2Mount ={
                {cosTau*cosRo, cosTau*sinRo, -sinTau},
                {-sinRo, cosRo, 0},
                {sinTau*cosRo, sinTau*sinRo, cosTau}};
        Matrix mount2body = new Matrix(arrayBody2Mount,3,3).transpose();

        double cosTheta = Math.cos(Math.toRadians(vehicleAtt.pitch));
        double sinTheta = Math.sin(Math.toRadians(vehicleAtt.pitch));
        double cosPsi = Math.cos(Math.toRadians(vehicleAtt.yaw));
        double sinPsi = Math.sin(Math.toRadians(vehicleAtt.yaw));
        double cosPhi = Math.cos(Math.toRadians(vehicleAtt.roll));
        double sinPhi = Math.sin(Math.toRadians(vehicleAtt.roll));

        double [][] arrayInertial2Body = {
                {cosTheta*cosPsi, cosTheta*sinPsi, -sinTheta},
                {sinPhi*sinTheta*cosPsi-cosPhi*sinPsi, sinPhi*sinTheta*sinPsi+cosPhi*cosPsi, sinPhi*cosTheta},
                {cosPhi*sinTheta*cosPsi+sinPhi*sinPsi, cosPhi*sinTheta*sinPsi-sinPhi*cosPsi, cosPhi*cosTheta}
        };

        Matrix body2inertial = new Matrix(arrayInertial2Body,3,3).transpose();

        double[][] imageCoordFocalArray = {
                {imageCoord.x},
                {imageCoord.y},
                {sensorFocal}
        };
        Matrix imageCoordFocalTemp = new Matrix(imageCoordFocalArray);
        Matrix imageCoordFocal = imageCoordFocalTemp.times(1/imageCoordFocalTemp.norm2());

        double norm = vehicleGeoLoc.z/(body2inertial.times(mount2body.times(image2mount.times(imageCoordFocal))).get(2,0));
        Matrix vecNED = body2inertial.times(mount2body.times(image2mount.times(imageCoordFocal))).times(norm);

        Coordinate nedResult = new Coordinate(vecNED.get(0,0),vecNED.get(1,0),vecNED.get(2,0));
        return GeoUtils.ned2geo(nedResult,vehicleGeoLoc);
    }

    public static Polygon getGeoSensorFootprint(Coordinate vehicleGeoLoc,
                                                            RollPitchYaw vehicleAtt,
                                                            Coordinate sensorOffset,
                                                            PanTilt sensorAtt,
                                                            int sensorWidth,
                                                            int sensorHeight,
                                                            int sensorFocal){

        Coordinate cornerTL = new Coordinate(-Math.floor(sensorWidth/2),-Math.floor(sensorHeight/2),0);
        Coordinate cornerTR = new Coordinate(-Math.floor(sensorWidth/2),Math.floor(sensorHeight/2),0);
        Coordinate cornerBL = new Coordinate(Math.floor(sensorWidth/2),-Math.floor(sensorHeight/2),0);
        Coordinate cornerBR = new Coordinate(Math.floor(sensorWidth/2),Math.floor(sensorHeight/2),0);

        Coordinate cornerTLGeo = getPixelCoordinates(vehicleGeoLoc,vehicleAtt,sensorOffset ,sensorAtt,sensorFocal,cornerTL);
        Coordinate cornerTRGeo = getPixelCoordinates(vehicleGeoLoc,vehicleAtt,sensorOffset ,sensorAtt,sensorFocal,cornerTR);
        Coordinate cornerBLGeo = getPixelCoordinates(vehicleGeoLoc,vehicleAtt,sensorOffset ,sensorAtt,sensorFocal,cornerBL);
        Coordinate cornerBRGeo = getPixelCoordinates(vehicleGeoLoc,vehicleAtt,sensorOffset ,sensorAtt,sensorFocal,cornerBR);

        Coordinate[] array = {cornerTLGeo,cornerTRGeo,cornerBRGeo,cornerBLGeo,cornerTLGeo};

        return new Polygon(array);
    }
}
