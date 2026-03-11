package dataaccess;

import model.AuthData;

public class SQLAuthDAO implements AuthDAO {

    public void createAuth(AuthData authData) {}

    public AuthData getAuth(String authToken) {
        return new AuthData("not an authToken", "not a username");
    }

    public void deleteAuth(String authToken) {}

    public void clear() {}

}
