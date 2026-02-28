package dataaccess;

import model.AuthData;

import java.util.ArrayList;

public class MemoryAuthDAO implements AuthDAO {

    private final ArrayList<AuthData> authList = new ArrayList<>();

    public void createAuth(AuthData auth) {authList.add(auth);}

    public AuthData getAuth(String authToken) throws DataAccessException {
        for (AuthData auth : authList) {
            if (auth.authToken().equals(authToken)) {return auth;}
        }
        throw new DataAccessException("AuthData DNE");
    }

    public void deleteAuth(String authToken) throws DataAccessException {
        for (AuthData auth : authList) {
            if (auth.authToken().equals(authToken)) {
                authList.remove(auth);
                return;
            }
        }
        throw new DataAccessException("AuthData cannot be deleted");
    }
}
