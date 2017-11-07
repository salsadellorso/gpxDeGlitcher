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

    private static final double CEILING = Double.MAX_VALUE;
    private static final double VICINITY_IN_TIME = 2;
    private static Integer outliersTotal = null;

    /**
     * for a given file specified by @param inputFileName
     * @return a GPX object cleared from bad points
     * @throws IOException
     */

    public static GPX smooth(String inputFileName, double desiredCutoff) throws IOException {
        List<WayPoint> originalPoints = new ArrayList<>();
        collectAllPoints(GPX.read(inputFileName), originalPoints);

        List<WayPoint> pointsFilteredBySpeed = new ArrayList<>();
        filterHorizontally(originalPoints, pointsFilteredBySpeed, desiredCutoff);

        GPX result = getGpx(pointsFilteredBySpeed);
        outliersTotal = originalPoints.size() - pointsFilteredBySpeed.size();
        return result;
    }

    private static void filterHorizontally(List<WayPoint> source, List<WayPoint> filtered, double desiredCutoff) {
        int ptsTotal = source.size();
        int i = 0;
        while (i < ptsTotal - 2) {
            WayPoint p1 = source.get(i);
            double speed = CEILING;
            int j = i;
            while (speed > desiredCutoff) {
                j++;
                if (j == ptsTotal - 1) break;
                WayPoint p2 = source.get(j);
                speed = getFlatSpeed(p1, p2);
            }
            filtered.add(source.get(j));
            i = j;
        }
    }

    private static GPX getGpx(List<WayPoint> source) {
        TrackSegment resultSegment = TrackSegment.of(source);
        Track resultTrack = Track.builder().addSegment(resultSegment).build();
        return GPX.builder().addTrack(resultTrack).build();
    }

    private static void collectAllPoints(GPX source, List<WayPoint> points) {
        List<Track> tracks = source.getTracks();
        for (Track track : tracks) {
            List<TrackSegment> segments = track.getSegments();
            for (TrackSegment segment : segments) {
                points.addAll(segment.getPoints());
            }
        }
    }

    /**
     * @return the number of bad points,
     * could be called only once and after {@link GpxDeGlitcher#smooth(java.lang.String, double)}
     * method
     */
    public static Integer numberOfPointsDeleted() {
        Integer pointsDeleted = new Integer(outliersTotal);
        outliersTotal = null;
        return pointsDeleted;
    }

    private static double getDistance(WayPoint p1, WayPoint p2) {
        return p1.distance(p2).doubleValue();
    }

    private static double getDuration(WayPoint p1, WayPoint p2) {
        return Duration.between(p1.getTime().get(), p2.getTime().get()).getSeconds();
    }

    private static double getFlatSpeed(WayPoint p1, WayPoint p2) {
        double duration = getDuration(p1, p2);
        if (duration < VICINITY_IN_TIME) return CEILING;
        return 3.6 * getDistance(p1, p2) / duration;
    }
}
