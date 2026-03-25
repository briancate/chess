package client;

import model.CreateResponse;
import model.GameData;
import model.RegisterResponse;
import model.UserData;

public class ServerFacade {

    private final ClientCommunicator clientCommunicator;

    public ServerFacade(String url) {
        // should this accept a url?
        // if it does, the url should have the root and port but not the last part
        clientCommunicator = new ClientCommunicator(url);
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

    public CreateResponse createGame(GameData gameData, String authToken) {
        try {
            return clientCommunicator.createGame(gameData, authToken);
        }
        catch (Exception ex) {
            System.out.println("CreateGame threw an exception: " + ex.getMessage() + " of type " + ex.getClass());
            return new CreateResponse(-1, ex.getMessage()); // Wait this isn't the same message...
        }
    }

//    public




}
