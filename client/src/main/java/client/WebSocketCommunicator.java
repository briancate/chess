package client;

import com.google.gson.Gson;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.Endpoint;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.MessageHandler;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;
import ui.Client;
import websocket.messages.LoadGame;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;
import websocket.messages.ServerMessageError;

import java.net.URI;

public class WebSocketCommunicator extends Endpoint {

    private final Session session;
    private final Gson gson = new Gson();


    public WebSocketCommunicator(String url, Client client) throws Exception {
        URI uri = new URI("ws://" + url + "/ws");
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, uri);

        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            public void onMessage(String message) {
                // turn the string into a ServerMessage and handle it from there
                ServerMessage serverMessage = gson.fromJson(message, ServerMessage.class);
                switch (serverMessage.getServerMessageType()) {
                    case ServerMessage.ServerMessageType.NOTIFICATION -> {
                        Notification notification = gson.fromJson(message, Notification.class);
                        client.displayNotification(notification);
                    }
                    case ServerMessage.ServerMessageType.LOAD_GAME -> {
                        LoadGame loadGame = gson.fromJson(message, LoadGame.class);
                        client.displayLoadGame(loadGame);
                    }
                    case ServerMessage.ServerMessageType.ERROR -> {
                        ServerMessageError error = gson.fromJson(message, ServerMessageError.class);
                        client.displayError(error);
                    }
                }
            }
        });
    }

    public void send(String msg) throws Exception {this.session.getBasicRemote().sendText(msg);}

    public void onOpen(Session session, EndpointConfig endpointConfig) {
        System.out.println("Connected!");
    }

    // It looks like this is where I should establish the WS connection
    // I think this then sends the WS requests
}
