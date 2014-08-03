package com.eloipereira.geoutils;

import Jama.Matrix;

/**
 * Created by eloi on 7/22/14.
 */
public final class GeoUtils {

    static double SEMI_MAJOR_AXIS = 6378137.0; // WGS84 earth semi-major axis in meters
    static double RECIPROCAL_FLATTENING = 298.257223563;
    static double SEMI_MINOR_AXIS = SEMI_MAJOR_AXIS*(1- 1/RECIPROCAL_FLATTENING);//6356752.3142;
    static double FIRST_ECCENTRICITY_SQUARE = 2*(1/RECIPROCAL_FLATTENING) - Math.pow((1/RECIPROCAL_FLATTENING),2); //6.69437999014*Math.pow(10,-3);
    static double SECOND_ECCENTRICITY_SQUARE = (1/RECIPROCAL_FLATTENING)*(2-(1/RECIPROCAL_FLATTENING))/Math.pow(1-(1/RECIPROCAL_FLATTENING),2); //6.73949674228*Math.pow(10,-3);

    private static double xi(double lat) {
        return Math.sqrt(1-FIRST_ECCENTRICITY_SQUARE*Math.pow(Math.sin(Math.toRadians(lat)),2));
    }

    public static Coordinate geodetic2ecef(Coordinate geoCoord){
        double phi = geoCoord.x;
        double lambda = geoCoord.y;
        double h = geoCoord.z;

        double cosPhi = Math.cos(Math.toRadians(phi));
        double cosLambda = Math.cos(Math.toRadians(lambda));
        double sinPhi = Math.sin(Math.toRadians(phi));
        double sinLambda = Math.sin(Math.toRadians(lambda));

        double x = (SEMI_MAJOR_AXIS/xi(phi)+h)*cosPhi*cosLambda;
        double y = (SEMI_MAJOR_AXIS/xi(phi)+h)*cosPhi*sinLambda ;
        double z = (SEMI_MAJOR_AXIS/xi(phi)*(1-FIRST_ECCENTRICITY_SQUARE)+h)*sinPhi;

        return new Coordinate(x,y,z);
    }

    public static Coordinate ecef2enu(Coordinate ecefCoord, Coordinate geoRef){

        double sinPhi = Math.sin(Math.toRadians(geoRef.x));
        double cosPhi = Math.cos(Math.toRadians(geoRef.x));
        double sinLambda = Math.sin(Math.toRadians(geoRef.y));
        double cosLambda = Math.cos(Math.toRadians(geoRef.y));

        double [][] array ={
                {-sinLambda, cosLambda, 0},
                {-sinPhi*cosLambda, -sinPhi*sinLambda, cosPhi},
                {cosPhi*cosLambda, cosPhi*sinLambda, sinPhi}
        };
        Matrix m = new Matrix(array,3,3);
        Coordinate ecefRef = geodetic2ecef(geoRef);
        Matrix result = m.times(ecefCoord.toColumnVector().minus(ecefRef.toColumnVector()));

        return new Coordinate(result.get(0,0),result.get(1,0),result.get(2,0));
    }

    public static Coordinate ecef2geodetic(Coordinate ecefCoord){
        double r2 = Math.pow(ecefCoord.x,2)+Math.pow(ecefCoord.y,2);
        double r = Math.sqrt(r2);
        double e2 = Math.pow(SEMI_MAJOR_AXIS,2) - Math.pow(SEMI_MINOR_AXIS,2);
        double f = 54*Math.pow(SEMI_MINOR_AXIS,2)*Math.pow(ecefCoord.z,2);
        double g = r2 + (1-FIRST_ECCENTRICITY_SQUARE)*Math.pow(ecefCoord.z,2)-e2*FIRST_ECCENTRICITY_SQUARE;
        double c = Math.pow(FIRST_ECCENTRICITY_SQUARE,2)*f*r2/Math.pow(g,3);
        double s = Math.pow(1+c+Math.sqrt(Math.pow(c,2)+2*c),1/3);
        double p = f/(3*Math.pow(s+1/s+1,2)*Math.pow(g,2));
        double q = Math.sqrt(1+2*Math.pow(FIRST_ECCENTRICITY_SQUARE,2)*p);
        double r0 = -(p*FIRST_ECCENTRICITY_SQUARE*r)/(1+q)+Math.sqrt(0.5*Math.pow(SEMI_MAJOR_AXIS,2)*(1+1/q)-p*(1-FIRST_ECCENTRICITY_SQUARE)*Math.pow(ecefCoord.z,2)/(q*(1+q))-0.5*p*r2);
        double tmp = Math.pow(r-FIRST_ECCENTRICITY_SQUARE*r0,2);
        double u = Math.sqrt(tmp+Math.pow(ecefCoord.z,2));
        double v = Math.sqrt(tmp+(1-FIRST_ECCENTRICITY_SQUARE)*Math.pow(ecefCoord.z,2));
        double z0 = Math.pow(SEMI_MINOR_AXIS,2)*ecefCoord.z/(SEMI_MAJOR_AXIS*v);
        double alt = u*(1-Math.pow(SEMI_MINOR_AXIS,2)/(SEMI_MAJOR_AXIS*v));
        double lat = Math.toDegrees(Math.atan((ecefCoord.z+SECOND_ECCENTRICITY_SQUARE*z0)/r));
        double lon = Math.toDegrees(Math.atan2(ecefCoord.y,ecefCoord.x));
        return new Coordinate(lat,lon,alt);
    }

    public static Coordinate enu2ecef(Coordinate enuCoord, Coordinate geoRef){

        double sinPhi = Math.sin(Math.toRadians(geoRef.x));
        double cosPhi = Math.cos(Math.toRadians(geoRef.x));
        double sinLambda = Math.sin(Math.toRadians(geoRef.y));
        double cosLambda = Math.cos(Math.toRadians(geoRef.y));

        double [][] array ={
                {-sinLambda, cosLambda, 0},
                {-sinPhi*cosLambda, -sinPhi*sinLambda, cosPhi},
                {cosPhi*cosLambda, cosPhi*sinLambda, sinPhi}
        };
        Matrix enu2ecef = new Matrix(array,3,3).transpose();

        Coordinate ecefRef = geodetic2ecef(geoRef);

        Matrix result = enu2ecef.times(enuCoord.toColumnVector()).plus(ecefRef.toColumnVector());
        return new Coordinate(result.get(0,0),result.get(1,0),result.get(2,0));
    }

    public static Coordinate enu2geo(Coordinate enuCoord, Coordinate geoRef){
        return ecef2geodetic(enu2ecef(enuCoord,geoRef));
    }

    public static Coordinate ned2geo(Coordinate nedCoord, Coordinate geoRef){
        return enu2geo(ned2enu(nedCoord),geoRef);
    }

    public static Coordinate enu2ned(Coordinate enuCoord){
        double [][] array = {
                {0,1,0},
                {1,0,0},
                {0,0,-1}};

        Matrix enu2ned = new Matrix(array);
        Matrix result = enu2ned.times(enuCoord.toColumnVector());
        return new Coordinate(result.get(0,0),result.get(1,0),result.get(2,0));
    }

    public static Coordinate ned2enu(Coordinate enuCoord){
        double [][] array = {
                {0,1,0},
                {1,0,0},
                {0,0,-1}};

        Matrix ned2enu = new Matrix(array).transpose();
        Matrix result = ned2enu.times(enuCoord.toColumnVector());
        return new Coordinate(result.get(0,0),result.get(1,0),result.get(2,0));
    }

}
