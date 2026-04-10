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
        // Notes from Dr. Wilkerson:
        // add a WebSocketHandler with the same structure as the other handlers
        // inside WebSocket (handler? service?) have a switch statement for the different UserGameCommands
        // then call the appropriate method

        // somewhere I need to have a data structure for different WebSocket connections
        // a map of gameID to a set of (connections? usernames?)
        // for notifications, just go through the set indiscriminately, don't notify the person who made the action

        try {
            configureDatabase();
        }
        catch (Exception ex) {
            System.out.println("Failed to initialize the database, RIP");
            throw new RuntimeException("Error: database is down");
        }

        boolean useSQL = true;
        if (useSQL) {
            this.authHandler = new AuthHandler(new SQLAuthDAO());
            this.userHandler = new UserHandler(new SQLUserDAO(), authHandler);
            this.gameHandler = new GameHandler(new SQLGameDAO(), authHandler);
        }
        // add a way for this to swap between memory and SQL
        else {
            this.authHandler = new AuthHandler(new MemoryAuthDAO());
            this.userHandler = new UserHandler(new MemoryUserDAO(), authHandler);
            this.gameHandler = new GameHandler(new MemoryGameDAO(), authHandler);
        }

        javalin = Javalin.create(config -> config.staticFiles.add("web"))

        // Register your endpoints and exception handlers here.
        .post("/user", userHandler::handleRegister)
        .post("/session", userHandler::handleLogin)
        .delete("/session", userHandler::handleLogout)
        .post("/game", gameHandler::handleCreate)
        .get("/game", gameHandler::listGames)
        .put("/game", gameHandler::handleJoin)
        .delete("/db", this::clear)
        .exception(DataAccessException.class, this::dataAccessHandler)
        .exception(ResponseException.class, this::responseHandler);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private void setHTTPStatusTo500IfStatusNotSet(Context ctx) {
        if (ctx.status().getCode() < 400) {
            ctx.status(500);
            ctx.result("{\"message\": \"" + "Error: the database is down" + "\"}");
        }
    }

    private void dataAccessHandler(DataAccessException ex, Context ctx) {
        setHTTPStatusTo500IfStatusNotSet(ctx);
        ctx.result("{\"message\": \"" + ex.getMessage() + "\"}");
    }

    private void responseHandler(ResponseException ex, Context ctx) {
        setHTTPStatusTo500IfStatusNotSet(ctx);
        ctx.result("{\"message\": \"" + ex.getMessage() + "\"}");
    }

    private void clear(Context ctx) throws server.ResponseException {
        gameHandler.clear(ctx);
        authHandler.clear(ctx);
        userHandler.clear(ctx);
    }

    private final String[] createStatements = {
//            """
//            DROP TABLE IF EXISTS users
//            """,
            """
            CREATE TABLE IF NOT EXISTS users (
            `username` varchar(50) NOT NULL,
            `password` varchar(100) NOT NULL,
            `email` varchar(50),
            PRIMARY KEY (`username`)
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS auths (
            `authtoken` varchar(50) NOT NULL,
            `username` varchar(50) NOT NULL,
            PRIMARY KEY (`authtoken`)
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS games (
            `gameid` int NOT NULL AUTO_INCREMENT,
            `whiteusername` varchar(50),
            `blackusername` varchar(50),
            `gamename` varchar(50) NOT NULL,
            `chessgame` TEXT,
            PRIMARY KEY (`gameid`)
            )
            """
    };

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Error: Unable to configure database.");
        }
    }
}
