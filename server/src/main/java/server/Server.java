package server;

import dataaccess.*;
import handlers.*;
import io.javalin.*;
import io.javalin.http.Context;
import java.sql.Connection;
import java.sql.SQLException;


public class Server {

    private final Javalin javalin;

    private final AuthHandler authHandler;
    private final UserHandler userHandler;
    private final GameHandler gameHandler;

    public Server() {

        // configureDatabase();
        // have the Server contructor throw the error?

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

    // add a configure database method

    // include a "create statements" string to do so
//    private final String[] createStatements = {
//            """
//            CREATE TABLE IF NOT EXISTS auths (all the info)
//            CREATE TABLE IF NOT EXISTS users (all the info)
//            CREATE TABLE IF NOT EXISTS games (all the info)
//            """
//    };

//    private void configureDatabase() throws ResponseException {
//        DatabaseManager.createDatabase();
//        try (Connection conn = DatabaseManager.getConnection()) {
//            for (String statement : createStatements) {
//                try (var preparedStatement = conn.prepareStatement(statement)) {
//                    preparedStatement.executeUpdate();
//                }
//            }
//        } catch (SQLException ex) {
//            throw new ResponseException(ResponseException.Code.ServerError, String.format("Unable to configure database: %s", ex.getMessage()));
//        }
//    }
}
