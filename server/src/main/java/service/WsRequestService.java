package service;

import handlers.ConnectionManager;
import websocket.commands.ConnectCommand;
import org.eclipse.jetty.websocket.api.Session;


public class WsRequestService {

    private final ConnectionManager connectionManager;

    public WsRequestService(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    public void connect(Session session, String username, ConnectCommand command) {

//        String ending = (!command.getIsPlayer())? "an observer.\n" : command.getTeamColor().toLowerCase();
        String message = username + " joined the game as " + command.getTeamColor();
        try {
            connectionManager.broadcast(session, command.getGameID(), message);
        }
        catch (Exception e) {
            throw new RuntimeException("Connect failed somehow lol" + e.getMessage() + e.getClass());
        }
    }

}
