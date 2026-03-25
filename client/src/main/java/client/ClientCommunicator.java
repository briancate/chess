package client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;

import chess.JoinResult;
import com.google.gson.Gson;

import model.*;


public class ClientCommunicator {

    // this should do all the communication with the actual server
    // so it should get request objects from the ServerFacade and send HTTP requests to the server
    // anything else? I assume a lot of error handling should happen here?

    // Create an HttpClient for making requests
    // This should be long-lived and shared, so a static final field is good here
    private final String serverURL;
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final Gson gson = new Gson();

    public ClientCommunicator(String url) {
        serverURL = url;
    }

    public RegisterResponse login(UserData userData, String path) throws Exception {
        String urlString = "http://" + serverURL + path;
        String jsonBody = gson.toJson(userData);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(urlString))
                .timeout(java.time.Duration.ofMillis(5000))
                .POST(BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (httpResponse.statusCode() >= 200 && httpResponse.statusCode() < 300) {
            return gson.fromJson(httpResponse.body(), RegisterResponse.class);
        } else {
            System.out.println("Error: received status code " + httpResponse.statusCode());
//            return gson.fromJson(httpResponse.body(), RegisterResponse.class);
            return new RegisterResponse (null, null, errorMessageFromStatusCode(httpResponse.statusCode()));
        }
    }

    public CreateResponse createGame(GameData gameData, String authToken) throws Exception {
        String urlString = "http://" + serverURL + "/game";
        String jsonBody = gson.toJson(gameData);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(urlString))
                .timeout(java.time.Duration.ofMillis(5000))
                .header("authorization", authToken)
                .POST(BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());


        if (httpResponse.statusCode() >= 200 && httpResponse.statusCode() < 300) {
            return gson.fromJson(httpResponse.body(), CreateResponse.class);
        } else {
            System.out.println("Error: received status code " + httpResponse.statusCode());
            return new CreateResponse (-1, errorMessageFromStatusCode(httpResponse.statusCode()));
        }
    }

    public ListGamesResponse listGames(String authToken) throws Exception {
        String urlString = "http://" + serverURL + "/game";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(urlString))
                .timeout(java.time.Duration.ofMillis(5000))
                .header("authorization", authToken)
                .GET()
                .build();

        HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (httpResponse.statusCode() >= 200 && httpResponse.statusCode() < 300) {
            return gson.fromJson(httpResponse.body(), ListGamesResponse.class);
        } else {
            System.out.println("Error: received status code " + httpResponse.statusCode());
            return new ListGamesResponse (null, errorMessageFromStatusCode(httpResponse.statusCode()));
        }
    }

    public JoinResult joinGame(JoinRequest joinRequest) throws Exception {
        String urlString = "http://" + serverURL + "/game";
        String jsonBody = gson.toJson(joinRequest);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(urlString))
                .timeout(java.time.Duration.ofMillis(5000))
                .header("authorization", joinRequest.authToken())
                .PUT(BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (httpResponse.statusCode() >= 200 && httpResponse.statusCode() < 300) {
            return null;
        } else {
            System.out.println("Error: received status code " + httpResponse.statusCode());
            return new JoinResult(errorMessageFromStatusCode(httpResponse.statusCode()));
        }

    }





    private String errorMessageFromStatusCode(int statusCode) {
//        if (statusCode == 400) {return "Error: unable to fulfill your request because it did not contain all needed information" +
//                " please make sure to fill in all field properly.";}
        if (statusCode == 400) {return "bad request";}
        if (statusCode == 401) {return "unauthorized";}
        if (statusCode == 403) {return "already taken";}
        else {return "internal database error";} // must be a 500
    }



}
