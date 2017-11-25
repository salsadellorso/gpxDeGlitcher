package gpswork;

import io.jenetics.jpx.*;
import io.jenetics.jpx.geom.Geoid;
import java.util.stream.Stream;

public class GpxWorker {
    private static final double ERROR_CODE = Double.NEGATIVE_INFINITY;

    /**
     * @param gpx
     * @return lenght of the tack in km
     */

    public static double getLength(GPX gpx) {
        if (gpx==null) return ERROR_CODE;
        return gpx.tracks()
                .flatMap(Track::segments)
                .findFirst()
                .map(TrackSegment::points).orElse(Stream.empty())
                .collect(Geoid.WGS84.toPathLength()).doubleValue() / 1000.0;
    }
}
