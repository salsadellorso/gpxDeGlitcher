import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.ZonedDateTime;

import controllers.UploadFileController;
import gpswork.GpxDeGlitcher;
import gpswork.exceptions.WeirdGpxException;
import io.jenetics.jpx.GPX;
import org.junit.*;

import static junit.framework.TestCase.assertTrue;

public class ApplicationTest {

    @Test
    public void testGpxDeGlitcher() {

        String filename = "yex.gpx";
        GPX fromGpxDeGlitcher = null;

        try {
            GPX gpx = GPX.builder()
                    .addTrack(track -> track
                            .addSegment(segment -> segment
                                    .addPoint(p -> p.lat(48.2081743).lon(16.3738189).ele(160).time(ZonedDateTime.now()))
                                    .addPoint(p -> p.lat(48.2081743).lon(16.3738189).ele(161).time(ZonedDateTime.now().plusMinutes(5)))
                                    .addPoint(p -> p.lat(48.2081743).lon(16.3738189).ele(162).time(ZonedDateTime.now().plusMinutes(5)))))
                    .build();
            GPX.write(gpx, filename);
            try {
                fromGpxDeGlitcher = GpxDeGlitcher.smooth(filename, 16, true);
            } catch (WeirdGpxException e) {
                e.printStackTrace();
            }

            //and no weird exceptions must be thrown from above code refering to jpx library!
            assertTrue(fromGpxDeGlitcher != null);

        } catch (IOException e) {
            System.out.println("o_o: smth weird happend, revisit testGpxDeGlitcher");
        } finally {
            new File(filename).delete();
        }

    }

}
