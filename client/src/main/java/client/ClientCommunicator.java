package client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;

import model.JoinResult;
import com.google.gson.Gson;

import model.*;


public class ClientCommunicator {

    // this should do all the communication with the actual server
    private final String serverURL;
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    private static final Gson GSON = new Gson();

    public ClientCommunicator(String url) {
        serverURL = url;
    }

    public RegisterResponse login(UserData userData, String path) throws Exception {
        String urlString = "http://" + serverURL + path;
        String jsonBody = GSON.toJson(userData);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(urlString))
                .timeout(java.time.Duration.ofMillis(5000))
                .POST(BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> httpResponse = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

        if (httpResponse.statusCode() >= 200 && httpResponse.statusCode() < 300) {
            return GSON.fromJson(httpResponse.body(), RegisterResponse.class);
        } else {
            return new RegisterResponse (null, null, errorMessageFromStatusCode(httpResponse.statusCode()));
        }
    }

    public LogoutResponse logout(String authToken) throws Exception {
        String urlString = "http://" + serverURL + "/session";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(urlString))
                .timeout(java.time.Duration.ofMillis(5000))
                .header("authorization", authToken)
                .DELETE()
                .build();

        HttpResponse<String> httpResponse = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

        if (httpResponse.statusCode() >= 200 && httpResponse.statusCode() < 300) {
            return null;
        } else {
            return new LogoutResponse(errorMessageFromStatusCode(httpResponse.statusCode()));
        }
    }

    public CreateResponse createGame(GameData gameData, String authToken) throws Exception {
        String urlString = "http://" + serverURL + "/game";
        String jsonBody = GSON.toJson(gameData);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(urlString))
                .timeout(java.time.Duration.ofMillis(5000))
                .header("authorization", authToken)
                .POST(BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> httpResponse = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());


        if (httpResponse.statusCode() >= 200 && httpResponse.statusCode() < 300) {
            return GSON.fromJson(httpResponse.body(), CreateResponse.class);
        } else {
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

        HttpResponse<String> httpResponse = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

        if (httpResponse.statusCode() >= 200 && httpResponse.statusCode() < 300) {
            return GSON.fromJson(httpResponse.body(), ListGamesResponse.class);
        } else {
            return new ListGamesResponse (null, errorMessageFromStatusCode(httpResponse.statusCode()));
        }
    }

    public JoinResult joinGame(JoinRequest joinRequest) throws Exception {
        String urlString = "http://" + serverURL + "/game";
        String jsonBody = GSON.toJson(joinRequest);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(urlString))
                .timeout(java.time.Duration.ofMillis(5000))
                .header("authorization", joinRequest.authToken())
                .PUT(BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> httpResponse = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

        if (httpResponse.statusCode() >= 200 && httpResponse.statusCode() < 300) {
            return null;
        } else {
            return new JoinResult(errorMessageFromStatusCode(httpResponse.statusCode()));
        }
    }

    public void clear() throws Exception {
        String urlString = "http://" + serverURL + "/db";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(urlString))
                .timeout(java.time.Duration.ofMillis(5000))
                .DELETE()
                .build();

        HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private String errorMessageFromStatusCode(int statusCode) {
        if (statusCode == 400) {return "bad request";}
        if (statusCode == 401) {return "unauthorized";}
        if (statusCode == 403) {return "already taken";}
        else {return "internal database error";} // must be a 500
    }
}
