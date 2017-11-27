package controllers;


import gpswork.GpxDeGlitcher;
import gpswork.exceptions.WeirdGpxException;
import storage.Statistics;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import play.data.DynamicForm;
import play.data.FormFactory;
import storage.Storage;
import play.mvc.*;
import java.io.*;
import views.html.*;
import javax.inject.Inject;
import static gpswork.GpxDeGlitcher.*;


public class UploadFileController extends Controller {

    //TODO: processFile method refers to Storage class directly, but using Guice seems messy

    @Inject
    private FormFactory formFactory;
    private static final String CUSTOM_ERROR_MESSAGE = "o_o: something went wrong";
    private static final Logger LOGGER = LogManager.getLogger("GLOBAL");

    public Result renderUploadFormPage() {
        return ok(uploadform.render());
    }

    /**
     * Passes uploaded file to static method {@link GpxDeGlitcher#smooth(java.lang.String, double, boolean)},
     * asks for a number of bad points and
     * then renders the result page
     */
    public Result processFile() {

        Http.MultipartFormData<File> body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart<File> preFile = body.getFile("fileField");
        String filepath = preFile.getFile().getAbsolutePath();

        DynamicForm requestData = formFactory.form().bindFromRequest();
        Double desiredCutoff = Double.parseDouble(requestData.get("desiredCutoff"));
        boolean isVertical = requestData.get("doVertical") != null;

        try {
            String status = processAndStore(filepath, desiredCutoff, isVertical) ? "SUCCESS!" : "FAIL!";
            if (isVertical) status += " Vertical filter has been applied.";
            return ok(resultgpx.render(status, new Statistics()));

        } catch (IOException ioe) {
            LOGGER.error("o_o: problems with uploaded file", ioe);
        } catch (WeirdGpxException wge) {
            LOGGER.error("o_o: this is a weird gpx", wge);
        } catch (IllegalStateException ise) {                    //rare case when file looks like xml
            LOGGER.error("o_o: false gpx/xml", ise);    //but not a valid xml (eg root is not closed, etc)
        }
        //   flash("error", "corrupted or missing file");        //needed for redirect
        return badRequest(errorpage.render(CUSTOM_ERROR_MESSAGE));
    }


    private boolean processAndStore(String filepath, Double desiredCutoff, boolean isVertical) throws IOException, WeirdGpxException {
        Storage.gpxSource = getGpxObject(filepath);
        Storage.gpxResult = smooth(filepath, desiredCutoff, isVertical);
        return Storage.gpxResult != null;
    }

}