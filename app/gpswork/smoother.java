package gpswork;

import controllers.UploadFileController;
import io.jenetics.jpx.GPX;
import io.jenetics.jpx.Track;
import io.jenetics.jpx.TrackSegment;
import io.jenetics.jpx.WayPoint;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import Storage.Storage;

public class smoother {

    public static final double CEILING = Double.MAX_VALUE;
    public static final double FLAT_CUTOFF = 15;
    public static final double VICINITY_IN_TIME = 2;

    public static double getDistance(WayPoint p1, WayPoint p2) {
        return p1.distance(p2).doubleValue();
    }

    public static double getDuration(WayPoint p1, WayPoint p2) {
        return Duration.between(p1.getTime().get(), p2.getTime().get()).getSeconds();
    }

    public static double getFlatSpeed(WayPoint p1, WayPoint p2) {
        double duration = getDuration(p1,p2);

        if (duration<VICINITY_IN_TIME) return CEILING;

        double distance = getDistance(p1,p2);

        double speed = 3.6 * (distance/duration);
        return speed;
    }

    public static String doTheWork(String inputFileName, String outputFileName) throws IOException {

        GPX source = GPX.read(inputFileName);
        System.out.println("File read succ");

        List<Track> tracks = source.getTracks();
        List<WayPoint> points = new ArrayList<WayPoint>();
        for(Track track:tracks) {
            List<TrackSegment> segments = track.getSegments();
            for(TrackSegment segment: segments) {
                points.addAll(segment.getPoints());
            }
        }

	    List<WayPoint> filteredBySpeed = new ArrayList<WayPoint>();

        int p = 0;

        while(p<points.size()-2) {

            WayPoint p1 = points.get(p);
            double speed = CEILING;
            int q = p;
            while(speed>FLAT_CUTOFF) {
                q++;
                if(q == points.size()-1) break;
                WayPoint p2 = points.get(q);
                speed = getFlatSpeed(p1,p2);
            }

            filteredBySpeed.add(points.get(q));
            p=q;
        }

        TrackSegment resultSegment = TrackSegment.of(filteredBySpeed);
        Track resultTrack = Track.builder().addSegment(resultSegment).build();

        GPX result = GPX.builder().addTrack(resultTrack).build();
        UploadFileController.resultstorage.put(outputFileName, result);

        GPX.write(result, outputFileName);
        UploadFileController.gpxResult = result;

        Storage.storage.put(outputFileName, new File(outputFileName));

        return String.format("%d", points.size()-filteredBySpeed.size());

        }

}
