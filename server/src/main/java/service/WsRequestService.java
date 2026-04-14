package service;

import com.google.gson.Gson;
import handlers.ConnectionManager;
import websocket.commands.ConnectCommand;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;


public class WsRequestService {

    private final ConnectionManager connectionManager;
    private final Gson gson = new Gson();


    public WsRequestService(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    public void connect(Session session, String username, ConnectCommand command) {

//        String ending = (!command.getIsPlayer())? "an observer.\n" : command.getTeamColor().toLowerCase();
        String message = username + " joined the game as " + command.getTeamColor().toLowerCase();
        Notification notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, message);
        String serializedNotification = gson.toJson(notification);
        try {
            connectionManager.broadcast(session, command.getGameID(), serializedNotification);
        }
        catch (Exception e) {
            throw new RuntimeException("Connect failed somehow lol" + e.getMessage() + e.getClass());
        }
    }

}
