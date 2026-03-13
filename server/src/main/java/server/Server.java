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

        try {
            configureDatabase();
        }
        catch (Exception ex) {
            System.out.println("Failed to initialize the database, RIP");
            throw new RuntimeException("Error: database is down");
        }

        this.authHandler = new AuthHandler(new SQLAuthDAO());
        this.userHandler = new UserHandler(new SQLUserDAO(), authHandler);
        this.gameHandler = new GameHandler(new SQLGameDAO(), authHandler);

        // add a way for this to swap between memory and SQL
//        this.authHandler = new AuthHandler(new MemoryAuthDAO());
//        this.userHandler = new UserHandler(new MemoryUserDAO(), authHandler);
//        this.gameHandler = new GameHandler(new MemoryGameDAO(), authHandler);


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

    private void dataAccessHandler(DataAccessException ex, Context ctx) {
        if (ctx.status().getCode() < 400) {
            ctx.status(500);
            ctx.result("{\"message\": \"" + "Error: the database is down" + "\"}");
        }
        ctx.result("{\"message\": \"" + ex.getMessage() + "\"}");
    }

    private void responseHandler(ResponseException ex, Context ctx) {
        if (ctx.status().getCode() < 400) {
            ctx.status(500);
            ctx.result("{\"message\": \"" + "Error: the database is down" + "\"}");
        }
        ctx.result("{\"message\": \"" + ex.getMessage() + "\"}");
    }

    private void clear(Context ctx) throws server.ResponseException {
        gameHandler.clear(ctx);
        authHandler.clear(ctx);
        userHandler.clear(ctx);
    }

    // add a configure database method

    // should the `json` field be a TEXT type instead? Or maybe a JSON type?
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
