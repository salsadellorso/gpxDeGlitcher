package controllers;

import gpswork.GpxDeGlitcher;
import storage.Storage;
import play.mvc.*;
import java.io.*;
import views.html.*;

import static gpswork.GpxDeGlitcher.pointsDeleted;
import static gpswork.GpxDeGlitcher.smooth;

public class UploadFileController extends Controller {

    //TODO: proocessFile method refers to Storage class, mb not good

    private static final String CUSTOM_ERROR_MESSAGE = "o_o: file is missing";


    public Result renderUploadFormPage() {
        return ok(uploadform.render());
    }

    /** Passes uploaded file to static method {@link GpxDeGlitcher#smooth(java.lang.String)}
     * then renders result page
     */

    public Result processFile() {

        Integer points = null;

        Http.MultipartFormData<File> body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart<File> preFile = body.getFile("fileField");

        if (preFile != null) {
            try{
               Storage.gpxResult = smooth(preFile.getFile().getAbsolutePath());
               points = Storage.pointsDeleted = pointsDeleted();
               //points = Storage.pointsDeleted;
                } catch(IOException e) {
                    e.printStackTrace();
                    System.out.println("o_o: problems with uploaded file");
                }

            String status = Storage.gpxResult == null ? "Fail": "Success";
            return ok(resultgpx.render(points, status));

        } else {
            flash("error!", "Missing file");
            return badRequest(CUSTOM_ERROR_MESSAGE);
        }
    }
}
