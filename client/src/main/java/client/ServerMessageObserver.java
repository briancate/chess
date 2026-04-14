package client;

import websocket.messages.LoadGame;
import websocket.messages.Notification;

public interface ServerMessageObserver {

    void displayNotification(Notification notification);

//    void displayError();

    void displayLoadGame(LoadGame loadGame);
}
