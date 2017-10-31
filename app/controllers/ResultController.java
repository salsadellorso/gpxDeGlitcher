package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.*;

public class ResultController extends Controller {

    public Result result(){
        return ok(resultgpx.render("chuka"));
    }
}
