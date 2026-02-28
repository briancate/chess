package dataaccess;

import model.UserData;

import java.util.ArrayList;

public class MemoryUserDAO implements UserDAO {

    private final ArrayList<UserData> userList = new ArrayList<>();

    public void createUser(UserData userData) throws DataAccessException {
        try {
            getUser(userData.username());
        }
        catch (DataAccessException e) { // find out why this doesn't work
            userList.add(userData);
            return;
        }
        throw new DataAccessException("Username already in database");
    }

    public UserData getUser(String username) throws DataAccessException {
        for (UserData user : userList) {
            if (user.username().equals(username)) {return user;}
        }
        throw new DataAccessException("User DNE");
    }

    public void deleteUser(String username) throws DataAccessException {
        for (UserData user : userList) {
            if (user.username().equals(username)) {
                userList.remove(user);
                return;
            }
        }
        throw new DataAccessException("User DNE");
    }

}
