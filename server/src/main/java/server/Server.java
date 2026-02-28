package server;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import io.javalin.*;
import io.javalin.http.Context;
import com.google.gson.Gson;
import model.AuthData;
import model.UserData;
import service.AuthService;
import service.UserService;


public class Server {

    private final Javalin javalin;

    private final AuthService authService;
    private final UserService userService;

    public Server() {
        // if mode = memory ...
        this.authService = new AuthService(new MemoryAuthDAO());
        this.userService = new UserService(new MemoryUserDAO());
        // else ... (SQL!)

        javalin = Javalin.create(config -> config.staticFiles.add("web"))

        // Register your endpoints and exception handlers here.
        .post("/user", this::register)
        .exception(DataAccessException.class, this::exceptionHandler);
    }

    private void register(Context ctx) throws DataAccessException {
        UserData userData = new Gson().fromJson(ctx.body(), UserData.class);
        this.userService.register(userData);
        String authToken = AuthService.generateToken();
        AuthData authData = new AuthData(authToken, userData.username());
        this.authService.createAuth(authData);
        String registerResult = "{\"username\": \"" + userData.username() + "\", \"authToken\": \"" + authToken + "\"}";

        ctx.result(registerResult);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private void exceptionHandler(DataAccessException ex, Context ctx) {
//        ctx.status(ex.toHttpStatusCode());
        ctx.status(404);
        ctx.result("{\"message\": \"" + ex.getMessage() + "\"}");
    }
}
