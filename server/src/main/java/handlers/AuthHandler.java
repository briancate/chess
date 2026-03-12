package handlers;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;
import server.ResponseException;
import service.AuthService;
import io.javalin.http.Context;


public class AuthHandler {

    private final AuthService authService;

    public AuthHandler(AuthDAO authDAO) {
        this.authService = new AuthService(authDAO);
    }

    public AuthData createAuth(String username, Context ctx) throws ResponseException {
        AuthData authData = new AuthData(AuthService.generateToken(), username);
        try {
            authService.createAuth(authData);
        }
        catch (ResponseException ex) {
            ctx.status(500);
            throw ex;
        }
        return authData;
    }

    public AuthData getAuth(String authToken) throws DataAccessException, ResponseException {
        return authService.getAuth(authToken);
    }

    public void deleteAuth(String authToken) throws ResponseException {
        authService.deleteAuth(authToken);
    }

    public void validateAuth(Context ctx) throws DataAccessException, ResponseException {
        String authToken = ctx.header("authorization");
        try {
            getAuth(authToken);
        }
        catch (DataAccessException ex) {
            ctx.status(401);
            throw ex;
        }
        catch (ResponseException ex) {
            ctx.status(500);
            throw ex;
        }
    }

    public void clear(Context ctx) throws ResponseException {
        try {
            authService.clear();
        }
        catch (ResponseException ex) {
            ctx.status(500);
            throw ex;
        }
    }

}
