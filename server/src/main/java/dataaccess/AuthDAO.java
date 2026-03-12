package dataaccess;

import model.AuthData;
import server.ResponseException;

public interface AuthDAO {

    void createAuth(AuthData auth) throws ResponseException;

    AuthData getAuth(String authToken) throws DataAccessException, ResponseException;

    void deleteAuth(String authToken) throws ResponseException;  // use a username instead?

    void clear() throws ResponseException;
}
