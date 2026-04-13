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
import websocket.commands.UserGameCommand;

//import jakarta.websocket.Session;
import org.eclipse.jetty.websocket.api.Session;



public class WsRequestHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final WsRequestService wsService = new WsRequestService();
    private final AuthHandler authHandler;
    private final ConnectionManager connectionManager = new ConnectionManager();
    private final Gson gson = new Gson();

    public WsRequestHandler(AuthHandler authHandler) {
        this.authHandler = authHandler;
    }

    @Override
    public void handleConnect(WsConnectContext ctx) {
        // somehow add the session to the connectionManager?
//        connectionManager.add(ctx.session);

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
            connectionManager.add(gameId, session);

            // replace these with actual method calls to the Service (once I implement those lol)
            switch (command.getCommandType()) {
                case CONNECT -> System.out.println("Connecting");
                case MAKE_MOVE -> System.out.println("Making a move");
                case LEAVE -> {
                    System.out.println("Leaving");
                    connectionManager.remove(gameId, session);
                }
                case RESIGN -> {
                    System.out.println("Resigning");
                    connectionManager.remove(gameId, session);
                }
            }
        }
        catch (Exception e) {
            // fail somehow
            // REMEMBER TO UPDATE THIS
            // DON'T SWALLOW EXCEPTIONS
            throw new RuntimeException("The WSRequest handler crashed somehow: " + e.getMessage() + e.getClass());
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        // somehow remove the session from the connectionManager?
//        connectionManager.remove();

        System.out.println("Websocket closed");
    }
}


