package handlers;

import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import service.UserService;
import io.javalin.http.Context;
import com.google.gson.Gson;
import java.util.Map;


public class UserHandler { // later do "extends Handler"

    private final UserService userService;
    private final AuthHandler authHandler;

    public UserData fromJsonToUserData(Context ctx) {
        return new Gson().fromJson(ctx.body(), UserData.class);
    }

    public UserHandler(UserDAO userDAO, AuthHandler authHandler) {
        this.userService = new UserService(userDAO);
        this.authHandler = authHandler;
    }

    public void handleRegister(Context ctx) throws DataAccessException {
        try {
            UserData userData = fromJsonToUserData(ctx);
            userService.register(userData);
            AuthData authData = authHandler.createAuth(userData.username());
            ctx.result(new Gson().toJson(Map.of("username", userData.username(), "authToken", authData.authToken())));
        }
        catch (DataAccessException ex) {
            ctx.status(403);
            throw ex;
        }
    }

    public void handleLogin(Context ctx) throws DataAccessException {
        UserData userData;
        UserData initialData = fromJsonToUserData(ctx);
        try {
            userData = userService.getUser(initialData.username());
        }
        catch (DataAccessException ex) {
            ctx.status(400);
            throw ex;
        }
        try {
            // remember to clean up the duplicate authData (either override or do something else)
            if (!initialData.password().equals(userData.password())) {throw new DataAccessException("Error: unauthorised");}
            AuthData authData = authHandler.createAuth(userData.username());
            ctx.result(new Gson().toJson(Map.of("username", userData.username(), "authToken", authData.authToken())));
        }
        catch (DataAccessException ex) {
            ctx.status(401);
            throw ex;
        }
    }

}
