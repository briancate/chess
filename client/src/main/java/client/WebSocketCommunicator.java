package client;

import com.google.gson.Gson;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.Endpoint;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.MessageHandler;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;
import ui.Client;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;

import java.net.URI;

public class WebSocketCommunicator extends Endpoint {

    private final Session session;
    private final Gson gson = new Gson();


    public WebSocketCommunicator(String url, Client client) throws Exception {
        URI uri = new URI("ws://" + url + "/ws");
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, uri);

        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            // turn this into a ServerMessage and handle it from there
            public void onMessage(String message) {
//                System.out.println(message);
                ServerMessage serverMessage = gson.fromJson(message, ServerMessage.class);

                switch (serverMessage.getServerMessageType()) {
                    case ServerMessage.ServerMessageType.NOTIFICATION -> {
                        Notification notification = gson.fromJson(message, Notification.class);
                        client.displayNotification(notification);
                        // send it back to the Client (at least the message)
                    }
                    case ServerMessage.ServerMessageType.LOAD_GAME -> {
                        // cry in a corner for the moment
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
