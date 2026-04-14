package client;

import websocket.messages.Notification;

public interface ServerMessageObserver {

    void displayNotification(Notification notification);

//    void displayError();

//    void displayLoadGame();
}
