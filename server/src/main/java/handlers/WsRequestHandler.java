package handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import org.jetbrains.annotations.NotNull;
import service.WsRequestService;
import websocket.commands.ConnectCommand;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessageError;


public class WsRequestHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final AuthHandler authHandler;
    private final ConnectionManager connectionManager = new ConnectionManager();
    private final Gson gson = new Gson();
    private final WsRequestService wsService = new WsRequestService(connectionManager);

    public WsRequestHandler(AuthHandler authHandler) {
        this.authHandler = authHandler;
    }

    @Override
    public void handleConnect(WsConnectContext ctx) {
        ctx.enableAutomaticPings();
        System.out.println("Websocket connected");
    }

    @Override
    public void handleMessage(@NotNull WsMessageContext ctx) {
        int gameID;
        Session session = ctx.session;

        try {
            UserGameCommand command = gson.fromJson(ctx.message(), UserGameCommand.class);
            gameID = command.getGameID();
            String username;
            try {
                username = (authHandler.getAuth(command.getAuthToken())).username();
            }
            catch (DataAccessException e) {
                ServerMessageError error = new ServerMessageError(e.getMessage());
                connectionManager.notifySingleSession(session, command.getGameID(), gson.toJson(error));
                return;
            }

            try {
                wsService.getSqlWsDAO().getGame(gameID);
            }
            catch (DataAccessException e) {
                ServerMessageError error = new ServerMessageError(e.getMessage());
                connectionManager.notifySingleSession(session, command.getGameID(), gson.toJson(error));
                return;
            }

            switch (command.getCommandType()) {
                case CONNECT -> {
                    ConnectCommand newCommand = gson.fromJson(ctx.message(), ConnectCommand.class);
                    connectionManager.add(gameID, session);
                    wsService.connect(session, username, newCommand);
                    wsService.loadGame(session, gameID);
                }
                case MAKE_MOVE -> {
                    MakeMoveCommand newCommand = gson.fromJson(ctx.message(), MakeMoveCommand.class);
                    wsService.makeMove(session, username, gameID, newCommand.getMove());
                }
                case LEAVE -> {
                    wsService.leaveGame(session, username, gameID);
                    connectionManager.remove(gameID, session);
                }
                case RESIGN -> {
                    wsService.resign(session, username, gameID);
                }
            }
        }
        catch (Exception e) {
            // REMEMBER TO UPDATE THIS
            // DON'T SWALLOW EXCEPTIONS
//            UserGameCommand command = gson.fromJson(ctx.message(), UserGameCommand.class);
//            gameID = command.getGameID();
//            ServerMessageError error = new ServerMessageError(e.getMessage());
//            connectionManager.notifySingleSession(session, gameID, gson.toJson(error));
//            throw new RuntimeException("The WSRequest handler crashed somehow: " + e.getMessage() + e.getClass());
            System.out.println("Error: somehow the server failed to send a ServerErrorMessage");
        }
    }

    @Override
    public void handleClose(@NotNull WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }
}


