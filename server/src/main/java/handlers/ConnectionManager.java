package handlers;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;


public class ConnectionManager {

    // an array list could have duplicates... I don't want duplicate sessions, but maybe it's okay?
    public final ConcurrentHashMap<Integer, CopyOnWriteArraySet<Session>> connections = new ConcurrentHashMap<>();

    public void add(Integer key, Session session) {
        // check if there's a set there already, create one if not
        connections.computeIfAbsent(key, k -> new CopyOnWriteArraySet<>());
        connections.get(key).add(session);
    }

    public void remove(Integer key, Session session) {
        connections.get(key).remove(session);
    }

    public void broadcast(Session exludedSession, int gameID, String message) throws IOException {
        // A BASIC SERVER MESSAGE HAS NO MESSAGE, THIS WON'T WORK
        // I think the ServerMessage object needs to have a message instance variable
        // and then for that variable to be the exact string I want to send
//        String msg = message.toString();
        for (Session c : connections.get(gameID)) {
            if (c.isOpen()) {
                // I might need to do null checking here eventually
                if (!c.equals(exludedSession)) {
                    c.getRemote().sendString(message);
                }
            }
        }
    }
}
