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
        InputStream is = Storage.getResultAsInputStream();
        Storage.gpxResult = null;
        Storage.numberOfPointsDeleted = null;
        // TODO: close InputStream?
        return is == null ? badRequest(CUSTOM_ERROR_MESSAGE) : ok(is);
    }

    /** method the request of gpxresult from OpenLayers script
     * @return gpx result as inputStream when OL script asks for it
     */

    public Result prepareResultToDrawOnMap() {
        InputStream is = Storage.getResultAsInputStream();
        // TODO: close InputStream?
        return is == null ? badRequest(CUSTOM_ERROR_MESSAGE) : ok(is);
    }

    public Result prepareSourceToDrawOnMap() {
        InputStream is = Storage.getSourceAsInputStream();
        return is == null ? badRequest(CUSTOM_ERROR_MESSAGE) : ok(is);
    }

}