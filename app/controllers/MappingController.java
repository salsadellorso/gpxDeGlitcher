package controllers;

import io.jenetics.jpx.GPX;
import play.mvc.Controller;
import play.mvc.Result;
import storage.Storage;

import java.io.*;

public class MappingController extends Controller {


    public Result processResult(){

        String resultGPX = null;

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            GPX.write(Storage.gpxResult, baos);
            InputStream is = new ByteArrayInputStream(baos.toByteArray());

            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }

            resultGPX = result.toString("UTF-8");


        } catch (IOException e){
            e.printStackTrace();
            System.out.println("o_o: cant handle actions in MappingController");
        }
        return ok(resultGPX);
    }





}
