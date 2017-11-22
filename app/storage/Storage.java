package storage;

import io.jenetics.jpx.GPX;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Storage {

    //TODO: seems weird solution for storage

    public static GPX gpxResult;
    public static Integer numberOfPointsDeleted;


    /** When method is called from ResultsController.downloadFileAsStream all static fields reset to null
     * @return InputStream representing result file
     */
    public static InputStream getResultAsInputStream() {
        InputStream is = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try{
            GPX.write(gpxResult, baos);
            is = new ByteArrayInputStream(baos.toByteArray());
        } catch (IOException e){
            e.printStackTrace();
            System.out.println("o_o: result GPX object is empty");
        }
        return is;
    }


}
