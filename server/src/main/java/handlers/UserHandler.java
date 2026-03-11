package handlers;

import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import service.UserService;
import io.javalin.http.Context;
import com.google.gson.Gson;
import java.util.Map;


public class UserHandler { // later do "extends Handler"?

    private final UserService userService;
    private final AuthHandler authHandler;
    private final Gson gson = new Gson();

    public UserData fromJsonToUserData(Context ctx) {
        return gson.fromJson(ctx.body(), UserData.class);
    }

    public UserHandler(UserDAO userDAO, AuthHandler authHandler) {
        this.userService = new UserService(userDAO);
        this.authHandler = authHandler;
    }

    public void handleRegister(Context ctx) throws DataAccessException {
        UserData userData = fromJsonToUserData(ctx);
        try {
            if (userData.username()==null || userData.password()==null) {throw new DataAccessException("Error: bad request");}
        }
        catch (DataAccessException ex) {
            ctx.status(400);
            throw ex;
        }

        try {
            userService.register(userData);
            AuthData authData = authHandler.createAuth(userData.username());
            ctx.result(gson.toJson(Map.of("username", userData.username(), "authToken", authData.authToken())));
        }
        catch (DataAccessException ex) {
            ctx.status(403);
            throw ex;
        }
    }

    public void handleLogin(Context ctx) throws DataAccessException {
        UserData userData;
        UserData initialData = fromJsonToUserData(ctx); // this won't have an email field, so it should be left blank

        try {
            if (initialData.username()==null || initialData.password()==null) {throw new DataAccessException("Error: bad request");}
        }
        catch (DataAccessException ex) {
            ctx.status(400);
            throw ex;
        }
        try {
            // there could be a duplicate authData here (as in two authData objects with the same username if you don't log out)
            userData = userService.getUser(initialData.username());
            if (!initialData.password().equals(userData.password())) {throw new DataAccessException("Error: unauthorised");}
            AuthData authData = authHandler.createAuth(userData.username());
            ctx.result(gson.toJson(Map.of("username", userData.username(), "authToken", authData.authToken())));
        }
        catch (DataAccessException ex) {
            ctx.status(401);
            throw ex;
        }
    }

    public void handleLogout(Context ctx) throws DataAccessException {
        String authToken = ctx.header("authorization");
        authHandler.validateAuth(ctx);
        authHandler.deleteAuth(authToken);
    }

    public void clear() {
        userService.clear();
    }
}
