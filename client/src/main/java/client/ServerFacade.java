package client;

import model.RegisterResponse;
import model.UserData;

public class ServerFacade {

    private final String serverURL;
    private final ClientCommunicator clientCommunicator;

    public ServerFacade(String url) {
        // should this accept a url?
        // if it does, the url should have the root and port but not the last part
        serverURL = url;
        clientCommunicator = new ClientCommunicator();
    }

    public RegisterResponse register(UserData userData) {
        // maybe to the to and from Json here?
        try {
            return clientCommunicator.login(userData, "/user");
        }
        catch (Exception ex) {
            System.out.println("Register threw an exception: " + ex.getMessage() + " of type " + ex.getClass());
        }
        return null; // BAD IDEA
    }

    public RegisterResponse login(UserData userData) {
        try {
            return clientCommunicator.login(userData, "/session");
        }
        catch (Exception ex) {
            System.out.println("Register threw an exception: " + ex.getMessage() + " of type " + ex.getClass());
        }
        return null; // BAD IDEA
    }




}
