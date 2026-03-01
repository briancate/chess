package service;

import dataaccess.AuthDAO;
import dataaccess.MemoryAuthDAO;
import model.AuthData;

import java.util.UUID;

public class AuthService {

    private final AuthDAO authDAO;

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public AuthService(AuthDAO authDAO) {this.authDAO = authDAO;}

    public void createAuth(AuthData auth) {authDAO.createAuth(auth);}

    // add a method to verify an authToken

}
