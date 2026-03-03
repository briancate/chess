package service;

import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.UserData;

public class UserService {

    private final UserDAO userDAO;

    public UserService(UserDAO userDAO) {this.userDAO = userDAO;}

    public void register(UserData userData) throws DataAccessException {
        userDAO.createUser(userData);
    }

    public UserData getUser(String username) throws DataAccessException {
        return userDAO.getUser(username);
    }

    public void clear() {
        userDAO.clear();
    }

}
