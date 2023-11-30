package edu.brown.cs32.livecode.dispatcher.handlers;

import edu.brown.cs32.livecode.dispatcher.handlers.AddHelpRequesterHandler.FailureResponse;
import edu.brown.cs32.livecode.dispatcher.handlers.AddHelpRequesterHandler.SuccessResponse;
import edu.brown.cs32.livecode.dispatcher.sessionState.SessionState;
import spark.Request;
import spark.Response;
import spark.Route;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DownloadInfoHandler implements Route {

    private SessionState sessionState;

    public DownloadInfoHandler(SessionState sessionState){
        this.sessionState = sessionState;

    }
    @Override
    public Object handle(Request request, Response response) throws Exception {
        if (sessionState.getRunning()) {
            return new FailureResponse("error_bad_request", "Download data after session has finished running.").serialize();
        }
        String info = request.queryParams("type");
        if (info == null){
            return new FailureResponse(
                    "error_bad_request", "Missing required parameter: type")
                    .serialize();

        }
        if (info.equals("all")){
            String filePath = "data/all-attendance.csv";
            byte[] content = Files.readAllBytes(Paths.get(filePath));
            try(OutputStream outputStream = response.raw().getOutputStream()){
                outputStream.write(content);
            }
            // download csv
            return new SuccessResponse("success", "All session information was downloaded" + filePath)
                    .serialize();

        }
        if (info.equals("current")){
            //download csv
            return new SuccessResponse("success", "This session's information was downloaded as a CSV")
                    .serialize();

        }
        return null;
    }
}
