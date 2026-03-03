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
        throw new DataAccessException("Error: already taken");
    }

    public UserData getUser(String username) throws DataAccessException {
        for (UserData user : userList) {
            if (user.username().equals(username)) {return user;}
        }
        throw new DataAccessException("Error: bad request");
    }

    public void deleteUser(String username) throws DataAccessException {
        for (UserData user : userList) {
            if (user.username().equals(username)) {
                userList.remove(user);
                return;
            }
        }
        throw new DataAccessException("Error: bad request");
    }

}
