package client;

import model.RegisterResponse;
import model.UserData;

public class ServerFacade {

    private final String serverURL;
    private final ClientCommunicator clientCommunicator;

    public ServerFacade(String url) {
        // should this accept a url?
        serverURL = url;
        clientCommunicator = new ClientCommunicator();
    }

    public RegisterResponse register(UserData userData) {
        // maybe to the to and from Json here?
        try {
            return clientCommunicator.register(userData);
        }
        catch (Exception ex) {
            System.out.println("Register threw an exception: " + ex.getMessage() + " of type " + ex.getClass());
        }
        return null; // BAD IDEA
        // or do I have a post method, a get method, etc.?
    }

    public RegisterResponse login(UserData userData) {
        try {
            return clientCommunicator.login(userData);
        }
        catch (Exception ex) {
            System.out.println("Register threw an exception: " + ex.getMessage() + " of type " + ex.getClass());
        }
        return null; // BAD IDEA
    }




}
