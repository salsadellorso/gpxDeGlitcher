package storage;

import static gpswork.GpxDeGlitcher.*;
import static gpswork.GpxWorker.getLength;

/**
 * holds all numbers to fill result page
 */

public class Statistics {

    //getters and setters are tiresome..

    public int pointsTotal;
    public int pointsDeleted;
    public double lon;
    public double lat;
    public double lenSource;
    public double lenResult;

    public Statistics(){
            pointsTotal = numberOfPointsTotal();
            pointsDeleted = numberOfPointsDeleted();
            lon = getLon();
            lat = getLat();
            lenSource = getLength(Storage.gpxSource);
            lenResult = getLength(Storage.gpxResult);
            }
}

