package client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.util.Locale;

import com.google.gson.Gson;

import model.RegisterResponse;
import model.UserData;



public class ClientCommunicator {

    // this should do all the communication with the actual server
    // so it should get request objects from the ServerFacade and send HTTP requests to the server
    // anything else? I assume a lot of error handling should happen here?

    // Create an HttpClient for making requests
    // This should be long-lived and shared, so a static final field is good here
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = new Gson();


    public static void main(String[] args) throws Exception {
//        new ClientCommunicator().get("localhost", 8080, "/name");
    }



    public RegisterResponse register(UserData userData) throws Exception {
        // should this accept a port as a parameter? Won't I always use localhost and port 8080?
        // and register will always use /user, so I don't think this needs any parameters
        String urlString = "http://localhost:8080/user";
        String jsonBody = gson.toJson(userData);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(urlString))
                .timeout(java.time.Duration.ofMillis(5000))
                .POST(BodyPublishers.ofString(jsonBody)) // HttpRequest.BodyPublishers.ofString(gson.toJson(userData)));
                .build();

        HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (httpResponse.statusCode() >= 200 && httpResponse.statusCode() < 300) {
            return gson.fromJson(httpResponse.body(), RegisterResponse.class);
        } else {
            System.out.println("Error: received status code " + httpResponse.statusCode());
            return null; // PROBABLY A BAD IDEA, CHANGE THIS
        }
    }

    public RegisterResponse login(UserData userData) throws Exception {
        String urlString = "http://localhost:8080/session";
        String jsonBody = gson.toJson(userData);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(urlString))
                .timeout(java.time.Duration.ofMillis(5000))
                .POST(BodyPublishers.ofString(jsonBody)) // HttpRequest.BodyPublishers.ofString(gson.toJson(userData)));
                .build();

        HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (httpResponse.statusCode() >= 200 && httpResponse.statusCode() < 300) {
            return gson.fromJson(httpResponse.body(), RegisterResponse.class);
        } else {
            System.out.println("Error: received status code " + httpResponse.statusCode());
            return null; // PROBABLY A BAD IDEA, CHANGE THIS
        }

    }


    private HttpRequest buildRequest() {
        return null;
    }

}
