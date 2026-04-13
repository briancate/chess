package client;

import model.JoinResult;
import model.*;

public class ServerFacade {

    private final ClientCommunicator clientCommunicator;
    private final WebSocketCommunicator webSocketCommunicator;

    public ServerFacade(int port) {
        // should this accept a url?
        // if it does, the url should have the root and port but not the last part
        clientCommunicator = new ClientCommunicator("localhost:" + port);
        try {
            webSocketCommunicator = new WebSocketCommunicator("localhost:" + port);
        } catch (Exception e) {
            throw new RuntimeException("Unable to establish the WebSocket Connection: " + e.getMessage() + e.getClass());
        }
    }

    public ClientCommunicator getClientCommunicator() {
        return clientCommunicator;
    }

    public WebSocketCommunicator getWebSocketCommunicator() {
        return webSocketCommunicator;
    }

    public RegisterResponse register(UserData userData) {
        try {
            return clientCommunicator.login(userData, "/user");
        }
        catch (Exception ex) {
            System.out.println("Register threw an exception: " + ex.getMessage() + " of type " + ex.getClass());
            return new RegisterResponse(null, null, ex.getMessage());
        }
    }

    public RegisterResponse login(UserData userData) {
        try {
            return clientCommunicator.login(userData, "/session");
        }
        catch (Exception ex) {
            System.out.println("Login threw an exception: " + ex.getMessage() + " of type " + ex.getClass());
            return new RegisterResponse(null, null, ex.getMessage());
        }
    }

    public LogoutResponse logout(String authToken) {
        try {
            return clientCommunicator.logout(authToken);
        }
        catch (Exception ex) {
            System.out.println("Logout threw an exception: " + ex.getMessage() + " of type " + ex.getClass());
            return new LogoutResponse(ex.getMessage());
        }
    }

    public CreateResponse createGame(GameData gameData, String authToken) {
        try {
            return clientCommunicator.createGame(gameData, authToken);
        }
        catch (Exception ex) {
            System.out.println("CreateGame threw an exception: " + ex.getMessage() + " of type " + ex.getClass());
            return new CreateResponse(-1, ex.getMessage()); // Wait this isn't the same message...
        }
    }

    public ListGamesResponse listGames(String authToken) {
        try {
            return clientCommunicator.listGames(authToken);
        }
        catch (Exception ex) {
            System.out.println("ListGames threw an exception: " + ex.getMessage() + " of type " + ex.getClass());
            return new ListGamesResponse(null, ex.getMessage());
        }
    }

    public JoinResult joinGame(JoinRequest joinRequest) {
        // this doesn't have a response body, so I guess it's chill?
        try {
            return clientCommunicator.joinGame(joinRequest);
        }
        catch (Exception ex) {
            System.out.println("JoinGame threw an exception: " + ex.getMessage() + " of type " + ex.getClass());
            return new JoinResult(ex.getMessage());
        }
    }
}
