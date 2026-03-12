package dataaccess;

import model.AuthData;
import server.ResponseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SQLAuthDAO implements AuthDAO {

    public void createAuth(AuthData authData) { // throws ResponseException
//        var statement = "INSERT INTO auths (authtoken, json) VALUES (?, ?)";
//        executeUpdate(statement, authData, json);
    }

//    public Pet addPet(Pet pet) throws ResponseException {
//        var statement = "INSERT INTO pet (name, type, json) VALUES (?, ?, ?)";
//        String json = new Gson().toJson(pet);
//        int id = executeUpdate(statement, pet.name(), pet.type(), json);
//        return new Pet(id, pet.name(), pet.type());
//    }

    public AuthData getAuth(String authToken) {
        return new AuthData("not an authToken", "not a username");
    }

    public void deleteAuth(String authToken) {}

    public void clear() {}

//    private void executeUpdate(String statement, AuthData authData, String json) throws ResponseException {
//        try (Connection conn = DatabaseManager.getConnection()) {
//            try (PreparedStatement ps = conn.prepareStatement(statement)) {
//                ps.setString(1, authData.authToken());
//                ps.setString(2, json);
//                ps.executeUpdate();
//            }
//        } catch (SQLException | DataAccessException e) {
//            throw new ResponseException("Unable to update database");
//        }
//    }

}
