package gpswork;


import io.jenetics.jpx.GPX;
import io.jenetics.jpx.Track;
import io.jenetics.jpx.TrackSegment;
import io.jenetics.jpx.WayPoint;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import storage.Storage;

public class GpxDeGlitcher {

    public static final double CEILING = Double.MAX_VALUE;
    public static double flat_cutoff;
    public static final double VICINITY_IN_TIME = 2;

    private static Integer outliersTotal = null;

    private static double getDistance(WayPoint p1, WayPoint p2) {
        return p1.distance(p2).doubleValue();
    }

    private static double getDuration(WayPoint p1, WayPoint p2) {
        return Duration.between(p1.getTime().get(), p2.getTime().get()).getSeconds();
    }

    private static double getFlatSpeed(WayPoint p1, WayPoint p2) {
        double duration = getDuration(p1,p2);
        if (duration<VICINITY_IN_TIME) return CEILING;
        return 3.6 * getDistance(p1,p2)/duration;
    }

    /**
     * for a given file specified by @param inputFileName
     * @return a GPX object cleared from bad points
     * @throws IOException
     */

    public static GPX smooth(String inputFileName, double desiredCutoff) throws IOException {
        flat_cutoff = desiredCutoff;
        GPX source = GPX.read(inputFileName);
        List<Track> tracks = source.getTracks();
        List<WayPoint> originalPoints = new ArrayList<>();

        //collecting all original points to one list
        for(Track track:tracks) {
            List<TrackSegment> segments = track.getSegments();
            for(TrackSegment segment: segments) {
                originalPoints.addAll(segment.getPoints());
            }
        }

	    List<WayPoint> pointsFilteredBySpeed = new ArrayList<>();
        int pointsTotal = originalPoints.size();
        int i = 0;

        //filtering out outliers
        while( i <pointsTotal-2) {
            WayPoint p1 = originalPoints.get(i);
            double speed = CEILING;
            int j = i;
            while(speed > flat_cutoff) {
                j++;
                if(j == pointsTotal-1) break;
                WayPoint p2 = originalPoints.get(j);
                speed = getFlatSpeed(p1,p2);
            }
            pointsFilteredBySpeed.add(originalPoints.get(j));
            i=j;
        }

        TrackSegment resultSegment = TrackSegment.of(pointsFilteredBySpeed);
        Track resultTrack = Track.builder().addSegment(resultSegment).build();
        GPX result = GPX.builder().addTrack(resultTrack).build();

        outliersTotal = pointsTotal - pointsFilteredBySpeed.size();
        return result;
        }

    /** @return the number of bad points,
     * could be called only once and after {@link GpxDeGlitcher#smooth(java.lang.String, double)}
     * method
     */
    public static Integer numberOfPointsDeleted(){
        Integer pointsDeleted = new Integer(outliersTotal);
        outliersTotal = null;
        return pointsDeleted;
    }
}
