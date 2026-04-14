package handlers;

import com.google.gson.Gson;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import org.jetbrains.annotations.NotNull;
import service.WsRequestService;
import websocket.commands.ConnectCommand;
import websocket.commands.UserGameCommand;

//import jakarta.websocket.Session;
import org.eclipse.jetty.websocket.api.Session;



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
        int gameId = -1;
        Session session = ctx.session;

        try {
            UserGameCommand command = gson.fromJson(ctx.message(), UserGameCommand.class);
            gameId = command.getGameID();
            String username = (authHandler.getAuth(command.getAuthToken())).username();

            // replace these with actual method calls to the Service (once I implement those lol)
            switch (command.getCommandType()) {
                case CONNECT -> {
                    ConnectCommand newCommand = gson.fromJson(ctx.message(), ConnectCommand.class);
                    connectionManager.add(gameId, session);
                    wsService.connect(session, username, newCommand);

//                    System.out.println("About to call loadGame");
                    wsService.loadGame(session, newCommand.getTeamColor(), gameId);
                    System.out.println("Connecting for real this time lol");
                }
                case MAKE_MOVE -> System.out.println("Making a move");
                case LEAVE -> {
                    System.out.println("Leaving");
                    connectionManager.remove(gameId, session);
                }
                case RESIGN -> {
                    System.out.println("Resigning");
//                    connectionManager.remove(gameId, session);
                }
            }
        }
        catch (Exception e) {
            // REMEMBER TO UPDATE THIS
            // DON'T SWALLOW EXCEPTIONS
            throw new RuntimeException("The WSRequest handler crashed somehow: " + e.getMessage() + e.getClass());
        }
    }

    @Override
    public void handleClose(@NotNull WsCloseContext ctx) {
        // somehow remove the session from the connectionManager?
        System.out.println("Websocket closed");
    }
}


