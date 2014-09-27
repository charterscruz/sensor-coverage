package com.eloipereira.sensorutils;

import com.eloipereira.geoutils.Coordinate;
import de.micromata.opengis.kml.v_2_2_0.Boundary;
import de.micromata.opengis.kml.v_2_2_0.Kml;
import de.micromata.opengis.kml.v_2_2_0.LinearRing;

/**
 * Created by eloi on 7/24/14.
 */
public class Polygon {
    Coordinate[] points;

    public Polygon(Coordinate[] points) {
        this.points = points;
    }

    public Coordinate[] getPoints() {
        return points;
    }

    public Kml toKml() {

        de.micromata.opengis.kml.v_2_2_0.Polygon poly = new de.micromata.opengis.kml.v_2_2_0.Polygon();

        Kml kml = new Kml();
        LinearRing ring = new LinearRing();

        for(Coordinate p: points){
            ring = ring.addToCoordinates(p.y,p.x);
        }

        poly.createAndSetOuterBoundaryIs().setLinearRing(ring);
        kml.createAndSetPlacemark().withName("Polygon").withOpen(true).setGeometry(poly);
        return kml;
    }

}
