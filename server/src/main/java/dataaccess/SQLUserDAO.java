package dataaccess;

import model.UserData;

public class SQLUserDAO implements UserDAO {

    public void createUser(UserData userData) throws DataAccessException {}

    public UserData getUser(String username) throws DataAccessException {
        return new UserData("not a username", "not a password", "not an email");
    }

    public void clear() {}

}
