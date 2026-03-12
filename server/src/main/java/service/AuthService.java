package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;
import server.ResponseException;

import java.util.UUID;

public class AuthService {

    private final AuthDAO authDAO;

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public AuthService(AuthDAO authDAO) {this.authDAO = authDAO;}

    public void createAuth(AuthData auth) throws ResponseException {
        // add a method at some point that removes any auths from a user with the given username
        authDAO.createAuth(auth);
    }

    public AuthData getAuth(String authToken) throws DataAccessException, ResponseException {
        return authDAO.getAuth(authToken);
    }

    public void deleteAuth(String authToken) throws ResponseException {
        authDAO.deleteAuth(authToken);
    }

    public void clear() throws ResponseException {
        authDAO.clear();
    }

}
