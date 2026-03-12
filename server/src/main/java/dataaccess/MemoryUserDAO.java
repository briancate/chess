package dataaccess;

import model.UserData;
import server.ResponseException;

import java.util.ArrayList;

public class MemoryUserDAO implements UserDAO {

    private final ArrayList<UserData> userList = new ArrayList<>();

    public void createUser(UserData userData) throws ResponseException, DataAccessException {
        try {
            getUser(userData.username());
        }
        catch (DataAccessException e) {
            userList.add(userData);
            return;
        }
        // you only reach here if the username is already taken
        throw new ResponseException("Error: already taken");
    }

    public UserData getUser(String username) throws DataAccessException {
        for (UserData user : userList) {
            if (user.username().equals(username)) {return user;}
        }
        throw new DataAccessException("Error: bad request");
    }

    public void clear() {
        userList.clear();
    }
}
