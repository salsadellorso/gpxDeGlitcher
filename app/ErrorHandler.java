import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import play.http.HttpErrorHandler;
import play.mvc.*;
import play.mvc.Http.*;
import views.html.errorpage;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import javax.inject.Singleton;

/** used only when Play itself spits out errors */

@Singleton
public class ErrorHandler implements HttpErrorHandler {

    private static final String CLIENT_ERROR = ")-: You did something wrong: ";
    private static final String SERVER_ERROR = "It's our fault: ";
    private static final Logger LOGGER = LogManager.getLogger("GLOBAL");

    public CompletionStage<Result> onClientError(RequestHeader request, int statusCode, String message) {
        LOGGER.error(CLIENT_ERROR + message);
        return CompletableFuture.completedFuture(
                Results.status(statusCode, errorpage.render(CLIENT_ERROR + message))
        );
    }

    public CompletionStage<Result> onServerError(RequestHeader request, Throwable exception) {
        LOGGER.error(SERVER_ERROR + exception.getMessage(), exception);
        return CompletableFuture.completedFuture(
                Results.internalServerError(errorpage.render(SERVER_ERROR ))
        );
    }
}