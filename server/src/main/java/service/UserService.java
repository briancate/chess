package service;

import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.UserData;
import service.AuthService;

public class UserService {

    private final UserDAO userDAO;

    public UserService(UserDAO userDAO) {this.userDAO = userDAO;}

    public void register(UserData userData) throws DataAccessException {
        userDAO.createUser(userData);
//        return ;
    }

    public UserData getUser(String username) throws DataAccessException {
        return userDAO.getUser(username);
    }




    //    public RegisterResult register(RegisterRequest registerRequest) {}
    //    public LoginResult login(LoginRequest loginRequest) {}
    //    public void logout(LogoutRequest logoutRequest) {}
}
