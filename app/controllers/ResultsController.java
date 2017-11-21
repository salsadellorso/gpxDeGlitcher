package controllers;

import io.jenetics.jpx.GPX;
import play.mvc.Controller;
import play.mvc.Result;
import storage.Storage;

import java.io.*;

public class ResultsController extends Controller {

    private static final String CUSTOM_ERROR_MESSAGE = "o_o: sorry, something has gone wrong";

    /**
     * Takes ready GPX object from the storage,
     * writes it to a (temporary) file
     * create an input stream out of this temporary file
     *
     * @return this stream with a response
     * name of a resulting file is specified in resultgpx template (see 'download' property)
     */

    public Result downloadFileAsStream() {

        GPX gpxResult = Storage.gpxResult;
        InputStream is = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            GPX.write(gpxResult, baos);
            is = new ByteArrayInputStream(baos.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("o_o: can't write or create InputStream in ResultsController.downloadFileAsStream()");
        } finally {
            Storage.gpxResult = null;
            Storage.numberOfPointsDeleted = null;
        }
        // TODO: close InputStream?
        return is == null || gpxResult == null ? badRequest(CUSTOM_ERROR_MESSAGE) : ok(is);
    }
}
