package server;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import handlers.AuthHandler;
import handlers.GameHandler;
import handlers.UserHandler;
import io.javalin.*;
import io.javalin.http.Context;



public class Server {

    private final Javalin javalin;

    private final AuthHandler authHandler;
    private final UserHandler userHandler;
    private final GameHandler gameHandler;

    public Server() {
        // add a way for this to swap between memory and SQL
        this.authHandler = new AuthHandler(new MemoryAuthDAO());
        this.userHandler = new UserHandler(new MemoryUserDAO(), authHandler);
        this.gameHandler = new GameHandler(new MemoryGameDAO(), authHandler);

//        this.authHandler = new AuthHandler(new SQLAuthDAO());
//        this.userHandler = new UserHandler(new SQLUserDAO(), authHandler);
//        this.gameHandler = new GameHandler(new SQLGameDAO(), authHandler);


        javalin = Javalin.create(config -> config.staticFiles.add("web"))

        // Register your endpoints and exception handlers here.
        .post("/user", userHandler::handleRegister)
        .post("/session", userHandler::handleLogin)
        .delete("/session", userHandler::handleLogout)
        .post("/game", gameHandler::handleCreate)
        .get("/game", gameHandler::listGames)
        .put("/game", gameHandler::handleJoin)
        .delete("/db", this::clear)
        .exception(DataAccessException.class, this::exceptionHandler);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private void exceptionHandler(DataAccessException ex, Context ctx) {
        // I should find a way to throw a 500 error if nothing else is met
        ctx.result("{\"message\": \"" + ex.getMessage() + "\"}");
    }

    private void clear(Context ctx) {
        gameHandler.clear();
        authHandler.clear();
        userHandler.clear();
    }
}
