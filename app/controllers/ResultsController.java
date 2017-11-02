package controllers;

import io.jenetics.jpx.GPX;
import play.mvc.Controller;
import play.mvc.Result;
import storage.Storage;

import java.io.*;

public class ResultsController extends Controller {

    private static final String CUSTOM_ERROR_MESSAGE = "o_o: sorry, something has gone wrong";
    private static final String TEMPORARY_FILE_NAME = "tmp" + System.currentTimeMillis()%666;

    /** Takes ready GPX object from the storage,
     * writes it to a (temporary) file
     * create an input stream out of this temporary file
     * @return this stream with a response
     * name of a resulting file is specified in resultgpx template (see 'download' property)
     */

    public Result downloadFileAsStream() {

        //TODO: try to return just a file without InputStream creation

        GPX gpxResult = Storage.gpxResult;
        InputStream is = null;
        try {
            GPX.write(gpxResult, TEMPORARY_FILE_NAME);
            File toReturn = new File(TEMPORARY_FILE_NAME);
            is = new FileInputStream(toReturn);
        } catch(IOException e) {
            e.printStackTrace();
            System.out.println("o_o: can't write or create InputStream in ResultsController.downloadFileAsStream()");
        }
        // TODO: close InputStream?
        // TODO: delete temporary and uploaded files?

        return is == null ? badRequest(CUSTOM_ERROR_MESSAGE): ok(is);
    }
}
