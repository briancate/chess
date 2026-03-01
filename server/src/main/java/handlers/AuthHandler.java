package handlers;

import dataaccess.AuthDAO;
import model.AuthData;
import service.AuthService;

public class AuthHandler {

    private final AuthService authService;

    public AuthHandler(AuthDAO authDAO) {
        this.authService = new AuthService(authDAO);
    }

    public AuthData createAuth(String username) {
        AuthData authData = new AuthData(AuthService.generateToken(), username);
        authService.createAuth(authData);
        return authData;
    }


}
