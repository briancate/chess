package handlers;

import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.UserData;
import service.UserService;

public class UserHandler { // later do "extends Handler"

    private final UserService userService;

    public UserHandler(UserDAO userDAO) {this.userService = new UserService(userDAO);}

    public void handleRegister(UserData userData) {
        try {
            userService.register(userData);
        }
        catch (DataAccessException ex) {
            // do something
        }
    }
}
