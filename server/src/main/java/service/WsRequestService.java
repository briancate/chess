package service;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import dataaccess.SQLWsDAO;
import handlers.ConnectionManager;
import websocket.commands.ConnectCommand;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.LoadGame;
import websocket.messages.Notification;


public class WsRequestService {

    private final ConnectionManager connectionManager;
    private final Gson gson = new Gson();
    private final SQLWsDAO sqlWsDAO = new SQLWsDAO();

    public WsRequestService(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    public void connect(Session session, String username, ConnectCommand command) {
        String message = username + " joined the game as " + command.getTeamColor().toLowerCase();
        Notification notification = new Notification(message);
        String serializedNotification = gson.toJson(notification);
        try {
            connectionManager.broadcast(session, command.getGameID(), serializedNotification);
        }
        catch (Exception e) {
            // Eventually change this to a ServerMessage.Error message
            throw new RuntimeException("Connect failed somehow lol" + e.getMessage() + e.getClass());
        }
    }

    public void loadGame(Session session, String teamColor, int gameID) throws Exception {
        try {
            ChessGame game = sqlWsDAO.getGame(gameID);
            if (teamColor.equals("an observer")) {teamColor = "WHITE";}
            LoadGame loadGame = new LoadGame(game, teamColor);
            String serializedLoadGame = gson.toJson(loadGame);
            // Add a second try / catch block here?
            connectionManager.notifySingleSession(session, gameID, serializedLoadGame);
        }
        catch (Exception ex) {
            // Eventually change this to a ServerMessage.Error message
            throw new Exception("Error: " + ex.getMessage());
        }
    }

    public void makeMove(Session session, String username, int gameID, ChessMove move, String teamColor) throws Exception {
        try {
            ChessGame game = sqlWsDAO.getGame(gameID);
            game.makeMove(move);

            // send everyone the updated game
            LoadGame loadGame = new LoadGame(game, teamColor);
            String serializedLoadGame = gson.toJson(loadGame);
            connectionManager.broadcast(null, gameID, serializedLoadGame);

            // save the game back to the database
            sqlWsDAO.updateGame(gameID, game);

            // notify everyone of the move
            String message = username + " made the move " + move;
            Notification notification = new Notification(message);
            String serializedNotification = gson.toJson(notification);
            connectionManager.broadcast(session, gameID, serializedNotification);
        }
        catch (Exception ex) {
            throw new Exception("Error: " + ex.getMessage());
        }
    }

}
