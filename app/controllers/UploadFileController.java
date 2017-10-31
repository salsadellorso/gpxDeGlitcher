package controllers;






import com.google.common.io.Files;
import gpswork.smoother;
import io.jenetics.jpx.Track;
import play.*;
import play.data.DynamicForm;
import play.mvc.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import Storage.Storage;

import views.html.*;
import play.data.FormFactory;

import javax.inject.Inject;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class UploadFileController extends Controller {

    private static int index = 0;
    private static String globalfilename;
    private static String globalsavedfilename;



    @Inject
    private FormFactory formFactory;

    public Result uploadFormPage() {
        return ok(uploadform.render());
    }

    public Result downloadFile(){
       return ok(new File(globalsavedfilename), false);

    }

    public Result uploadFormPost(){
        String points = "NULL";

        Http.MultipartFormData<File> body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart<File> myFile = body.getFile("fileField");
        if (myFile != null) {
            String fileName = myFile.getFilename();
            String contentType = myFile.getContentType();
            File file = myFile.getFile();
            long size = file.length();
            String path = file.getPath();
            File savedFile = new File(String.format("%d-%s", index, fileName));
            try{Files.copy(file, savedFile);


            globalfilename = String.format("%d-%s", index, fileName);
            globalsavedfilename = String.format("%s-%s", globalfilename, "result");

            points = smoother.doTheWork(globalfilename, globalsavedfilename);
          //  Files.copy(savedFile, new File(globalsavedfilename));
            index++;



            }
            catch(IOException e) {System.out.println("ass");}

            return ok(resultgpx.render(points));

        } else {
            flash("error!", "Missing file");
            return badRequest();
        }

        /*DynamicForm requestData = formFactory.form().bindFromRequest();
        String stringrequest = requestData.get("fileField");
        File file = request().body().asRaw().asFile();
        String fileSize = file.getName();*/
        //return ok();

   }



}
