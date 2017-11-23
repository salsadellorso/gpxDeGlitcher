package controllers;


import gpswork.GpxDeGlitcher;
import play.data.DynamicForm;
import play.data.FormFactory;
import storage.Storage;
import play.mvc.*;

import java.io.*;

import views.html.*;


import javax.inject.Inject;

import static gpswork.GpxDeGlitcher.numberOfPointsDeleted;
import static gpswork.GpxDeGlitcher.smooth;
import static gpswork.GpxDeGlitcher.getGpxObject;

public class UploadFileController extends Controller {

    //TODO: processFile method refers to Storage class directly, but using Guice seems messy

    @Inject
    private FormFactory formFactory;
    private static final String CUSTOM_ERROR_MESSAGE = "o_o: file is missing";


    public Result renderUploadFormPage() {
        return ok(uploadform.render());
    }

    /**
     * Passes uploaded file to static method {@link GpxDeGlitcher#smooth(java.lang.String, double, boolean)},
     * asks for a number of bad points and
     * then renders the result page
     */
    public Result processFile() {

        Integer points = null;
        String resultsPath = null;

        Http.MultipartFormData<File> body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart<File> preFile = body.getFile("fileField");

        DynamicForm requestData = formFactory.form().bindFromRequest();
        Double desiredCutoff = Double.parseDouble(requestData.get("desiredCutoff"));
        boolean isVertical = requestData.get("doVertical") != null;
        String filepath = preFile.getFile().getAbsolutePath();

        if (preFile != null) {
            try {
                Storage.gpxSource = getGpxObject(filepath);
                Storage.gpxResult = smooth(filepath, desiredCutoff, isVertical);
                points = Storage.numberOfPointsDeleted = numberOfPointsDeleted();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("o_o: problems with uploaded file");
            }

            boolean resultExists = Storage.gpxResult == null;
            String status = resultExists ? "Fail" : "Success";
            if (resultExists && isVertical) status += "/n vertical filter has been applied";
            return ok(resultgpx.render(points, status));

        } else {
            flash("error!", "Missing file");
            return badRequest(CUSTOM_ERROR_MESSAGE);
        }
    }
}
