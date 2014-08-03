package com.eloipereira.geoutils

import org.scalatest.FunSuite


/**
 * Created by eloi on 7/23/14.
 */
class TestCoordinates extends FunSuite {
  test("Geodetic to ECEF"){
    val originGEO = new Coordinate(0.0,0.0,0.0);
    val originGEOinECEF = new Coordinate(6378137.0,0.0,0.0);
    val epsilon = 0.00001
    val error = originGEOinECEF.toColumnVector.minus(GeoUtils.geodetic2ecef(originGEO).toColumnVector).norm2()
    assert(error < epsilon)
  }

  test("ECEF to ENU"){
    val refGEO = new Coordinate(0.0,0.0,0.0);
    val testECEF = new Coordinate(6378137.0,0.0,0.0);
    val originENU = new Coordinate(0.0,0.0,0.0);
    val epsilon = 0.00001
    val error = originENU.toColumnVector.minus(GeoUtils.ecef2enu(testECEF,refGEO).toColumnVector).norm2()
    assert(error < epsilon)
  }

  test("ENU to ECEF"){
    val refGEO = new Coordinate(0.0,0.0,0.0);
    val testECEF = new Coordinate(6378137.0,0.0,0.0);
    val originENU = new Coordinate(0.0,0.0,0.0);
    val epsilon = 0.00001
    val error = testECEF.toColumnVector.minus(GeoUtils.enu2ecef(originENU,refGEO).toColumnVector).norm2()
    assert(error < epsilon)
  }



  test("ECEF to Geodetic"){
    val originGEO = new Coordinate(0.0,0.0,0.0);
    val originGEOinECEF = new Coordinate(6378137.0,0.0,0.0);
    val epsilon = 0.00001
    val error = originGEO.toColumnVector.minus(GeoUtils.ecef2geodetic(originGEOinECEF).toColumnVector).norm2()
    assert(error < epsilon)
  }

  test("ECEF to Geodetic North Pole"){
    val northPoleGeo = new Coordinate(90.0,0.0,0.0);
    val northPoleGeoInECEF = new Coordinate(0.0,0.0,GeoUtils.SEMI_MINOR_AXIS);
    val epsilon = 0.00001
    val error = northPoleGeo.toColumnVector.minus(GeoUtils.ecef2geodetic(northPoleGeoInECEF).toColumnVector).norm2()
    assert(error < epsilon)
  }

  test("ENU to NED"){
    val testENU = new Coordinate(1.0,2.0,3.0);
    val testNED = new Coordinate(2.0,1.0,-3.0);
    val epsilon = 0.00001
    val error = testNED.toColumnVector.minus(GeoUtils.enu2ned(testENU).toColumnVector).norm2()
    assert(error < epsilon)
  }


}
