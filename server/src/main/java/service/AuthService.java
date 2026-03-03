package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;

import java.util.UUID;

public class AuthService {

    private final AuthDAO authDAO;

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public AuthService(AuthDAO authDAO) {this.authDAO = authDAO;}

    public void createAuth(AuthData auth) {authDAO.createAuth(auth);}

    public void getAuth(String authToken) throws DataAccessException {
        authDAO.getAuth(authToken);
    }

    public void deleteAuth(String authToken) throws DataAccessException {
        authDAO.deleteAuth(authToken);
    }

    // add a method to verify an authToken

}
