package gpswork;


import io.jenetics.jpx.GPX;
import io.jenetics.jpx.Track;
import io.jenetics.jpx.TrackSegment;
import io.jenetics.jpx.WayPoint;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class GpxDeGlitcher {

    private static final double CEILING = Double.MAX_VALUE;
    private static final double VICINITY_IN_TIME = 1.4;
    private static final double VERTICAL_CUTOFF = 2.4;
    private static final boolean VERTICAL = true;

    private static Integer outliersTotal = null;
    private static Integer pointsTotal = null;
    private static Double lon = null;
    private static Double lat = null;


    /**
     * for a given file specified by @param inputFileName
     *
     * @return a GPX object cleared from bad points
     * @throws IOException
     */

    public static GPX smooth(String inputFileName, double desiredCutoff, boolean isVertical) throws IOException {

        GPX horizontallyFiltered = smooth(inputFileName, desiredCutoff);
        if (!isVertical) return horizontallyFiltered;

        List<WayPoint> originalPoints = new ArrayList<>();
        collectAllPoints(horizontallyFiltered, originalPoints);

        //scan through the points second time to filter vertical outbreaks
        List<WayPoint> pointsFilteredVert = new ArrayList<>();
        filter(originalPoints, pointsFilteredVert, desiredCutoff, VERTICAL);
        outliersTotal = pointsTotal - pointsFilteredVert.size();
        return getGpx(pointsFilteredVert);
    }

    private static GPX smooth(String inputFileName, double desiredCutoff) throws IOException {
        List<WayPoint> originalPoints = new ArrayList<>();
        collectAllPoints(GPX.read(inputFileName), originalPoints);
        pointsTotal = originalPoints.size();

        List<WayPoint> pointsFilteredBySpeed = new ArrayList<>();
        filter(originalPoints, pointsFilteredBySpeed, desiredCutoff, !VERTICAL);
        outliersTotal = pointsTotal - pointsFilteredBySpeed.size();
        return getGpx(pointsFilteredBySpeed);
    }

    //since this is the only time we access points directly,
    //lat and lon get their values inside this method

    private static void filter(List<WayPoint> source, List<WayPoint> filtered, double desiredCutoff, boolean isVertical) {
        double cutoff = isVertical ? VERTICAL_CUTOFF : desiredCutoff;
        int ptsTotal = source.size();
        int i = 0;
        WayPoint center = source.get(ThreadLocalRandom.current().nextInt(0, pointsTotal + 1));
        lon = center.getLongitude().doubleValue();
        lat = center.getLatitude().doubleValue();
        while (i < ptsTotal - 2) {
            WayPoint p1 = source.get(i);
            double speed = CEILING;
            int j = i;
            while (speed > cutoff) {
                j++;
                if (j == ptsTotal - 1) break;
                WayPoint p2 = source.get(j);
                speed = isVertical ? getVerticalSpeed(p1, p2) : getFlatSpeed(p1, p2);
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

    /**
     * @return the Total numbe of points,
     * could be called only once and after {@link GpxDeGlitcher#smooth(java.lang.String, double)}
     * method
     */
    public static int numberOfPointsTotal() {
        int pointsInitially = new Integer(pointsTotal);
        pointsTotal = null;
        return pointsInitially;

    }

    /**
     * @return coords of current track to show in view
     */

    public static double getLon() {
        double longti = new Double(lon);
        lon = null;
        return longti;
    }

    public static double getLat() {
        double lati = new Double(lat);
        lat = null;
        return lati;
    }

    public static GPX getGpxObject(String filepath) {
        GPX gpxObject = null;
        try {
            gpxObject = GPX.read(filepath);
        } catch (IOException e) {
            System.out.println("o_o: can't read source file");
        }
        return gpxObject;
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

    private static double getVerticalSpeed(WayPoint p1, WayPoint p2) {
        double duration = getDuration(p1, p2);
        if (duration < VICINITY_IN_TIME) return VERTICAL_CUTOFF;
        if (!p1.getElevation().isPresent() || !p2.getElevation().isPresent()) return 0.0;
        return 3.6 * Math.abs(p1.getElevation().get().doubleValue() - p2.getElevation().get().doubleValue()) / getDuration(p1, p2);
    }

    //TODO: .isPresent() and all getVertSpeed stuff looks messy
    //TODO: put all calcs in one big method with a flag for vertical filtering

}
