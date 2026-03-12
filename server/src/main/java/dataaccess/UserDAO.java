package dataaccess;

import model.UserData;
import server.ResponseException;

public interface UserDAO {

    void createUser(UserData userData) throws ResponseException, DataAccessException;

    UserData getUser(String username) throws DataAccessException, ResponseException;

    void clear() throws ResponseException;
}
