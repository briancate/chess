package service;

import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.UserData;
import server.ResponseException;

public class UserService {

    private final UserDAO userDAO;

    public UserService(UserDAO userDAO) {this.userDAO = userDAO;}

    public void register(UserData userData) throws ResponseException, DataAccessException {
        userDAO.createUser(userData);
    }

    public UserData getUser(String username) throws DataAccessException, ResponseException {
        return userDAO.getUser(username);
    }

    public void clear() throws ResponseException {
        userDAO.clear();
    }

}
