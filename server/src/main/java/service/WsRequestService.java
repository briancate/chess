package service;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import dataaccess.SQLWsDAO;
import handlers.ConnectionManager;
import model.GameData;
import websocket.commands.ConnectCommand;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.LoadGame;
import websocket.messages.Notification;
import websocket.messages.ServerMessageError;


public class WsRequestService {

    private final ConnectionManager connectionManager;
    private final Gson gson = new Gson();
    private final SQLWsDAO sqlWsDAO = new SQLWsDAO();

    public WsRequestService(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    public SQLWsDAO getSqlWsDAO() {
        return sqlWsDAO;
    }

    public void connect(Session session, String username, ConnectCommand command) throws Exception {
        String teamColor;
        if (command.getTeamColor() == null) {teamColor = "an observer";}
        else {teamColor = command.getTeamColor();}
        String message = username + " joined the game as " + teamColor;
        // maybe check the DAO to see if the username shows up?
        Notification notification = new Notification(message);
        String serializedNotification = gson.toJson(notification);
        try {
            connectionManager.broadcast(session, command.getGameID(), serializedNotification);
        }
        catch (Exception e) {
            ServerMessageError error = new ServerMessageError(e.getMessage());
            connectionManager.notifySingleSession(session, command.getGameID(), gson.toJson(error));
        }
    }

    public void loadGame(Session session, int gameID) throws Exception {
        try {
            ChessGame game = sqlWsDAO.getGame(gameID).game();
            LoadGame loadGame = new LoadGame(game);
            // Add a second try / catch block here?
            connectionManager.notifySingleSession(session, gameID, gson.toJson(loadGame));
        }
        catch (Exception ex) {
            ServerMessageError error = new ServerMessageError(ex.getMessage());
            connectionManager.notifySingleSession(session, gameID, gson.toJson(error));
        }
    }

    public void leaveGame(Session session, String username, int gameID) throws Exception {
        try {
            sqlWsDAO.removeUser(gameID, username);
            System.out.println("Just after the sql Remove");
            Notification notification = new Notification(username + " left the game");
            connectionManager.broadcast(session, gameID, gson.toJson(notification));
            System.out.println("Should have been notified");
        }
        catch (Exception e) {
            ServerMessageError error = new ServerMessageError(e.getMessage());
            connectionManager.notifySingleSession(session, gameID, gson.toJson(error));
        }
    }

    public void resign(Session session, String username, int gameID) throws Exception {
        try {
            GameData gameData = sqlWsDAO.getGame(gameID);
            ChessGame game = gameData.game();

            if (game.getIsFinished()) {
                ServerMessageError error = new ServerMessageError("Unable to resign once the game is over");
                connectionManager.notifySingleSession(session, gameID, gson.toJson(error));
                return;
            }

            markGameAsFinished(gameID, game);
            Notification notification = new Notification(username + " has resigned!");
            connectionManager.broadcast(null, gameID, gson.toJson(notification));
        }
        catch (Exception ex) {
            ServerMessageError error = new ServerMessageError(ex.getMessage());
            connectionManager.notifySingleSession(session, gameID, gson.toJson(error));
        }
    }

    public void makeMove(Session session, String username, int gameID, ChessMove move) throws Exception {
        try {
            GameData gameData = sqlWsDAO.getGame(gameID);
            ChessGame game = gameData.game();

            if (game.getIsFinished()) {
                ServerMessageError error = new ServerMessageError("Unable to make moves once game is over");
                connectionManager.notifySingleSession(session, gameID, gson.toJson(error));
                return;
            }

            game.makeMove(move);

            // send everyone the updated game
            LoadGame loadGame = new LoadGame(game);
            connectionManager.broadcast(null, gameID, gson.toJson(loadGame));

            // save the game back to the database
            sqlWsDAO.updateGame(gameID, game);

            // notify everyone of the move
            String message = username + " made the move " + move;
            Notification notification = new Notification(message);
            connectionManager.broadcast(session, gameID, gson.toJson(notification));

            // check for check, stalemate, and checkmate (not in that order)
            if (game.isInCheckmate(ChessGame.TeamColor.WHITE)) {
                Notification notification1 = new Notification(gameData.whiteUsername() + " is in checkmate!");
                connectionManager.broadcast(null, gameID, gson.toJson(notification1));
                markGameAsFinished(gameID, game);
                return;
            }
            else if (game.isInCheckmate(ChessGame.TeamColor.BLACK)) {
                Notification notification1 = new Notification(gameData.blackUsername() + " is in checkmate!");
                connectionManager.broadcast(null, gameID, gson.toJson(notification1));
                markGameAsFinished(gameID, game);
                return;
            }
            if (game.isInCheck(ChessGame.TeamColor.WHITE)) {
                Notification notification1 = new Notification(gameData.whiteUsername() + " is in check!");
                connectionManager.broadcast(null, gameID, gson.toJson(notification1));
            }
            else if (game.isInCheck(ChessGame.TeamColor.BLACK)) {
                Notification notification1 = new Notification(gameData.blackUsername() + " is in check!");
                connectionManager.broadcast(null, gameID, gson.toJson(notification1));
            }
            if (game.isInStalemate(ChessGame.TeamColor.WHITE)) {
                Notification notification1 = new Notification(gameData.whiteUsername() + " is in stalemate!");
                connectionManager.broadcast(null, gameID, gson.toJson(notification1));
                markGameAsFinished(gameID, game);
            }
            else if (game.isInStalemate(ChessGame.TeamColor.BLACK)) {
                Notification notification1 = new Notification(gameData.blackUsername() + " is in stalemate!");
                connectionManager.broadcast(null, gameID, gson.toJson(notification1));
                markGameAsFinished(gameID, game);
            }
        }
        catch (Exception ex) {
            ServerMessageError error = new ServerMessageError(ex.getMessage());
            System.out.println(ex.getMessage());
            connectionManager.notifySingleSession(session, gameID, gson.toJson(error));
        }
    }

    public void markGameAsFinished(int gameID, ChessGame game) throws Exception {
        game.setIsFinished(true);
        sqlWsDAO.updateGame(gameID, game);
    }

}
