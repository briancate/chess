package client;

import websocket.messages.LoadGame;
import websocket.messages.Notification;
import websocket.messages.ServerMessageError;

public interface ServerMessageObserver {

    void displayNotification(Notification notification);

    void displayError(ServerMessageError error);

    void displayLoadGame(LoadGame loadGame);
}
