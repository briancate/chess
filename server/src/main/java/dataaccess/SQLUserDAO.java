package dataaccess;

import model.AuthData;
import model.UserData;
import server.ResponseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SQLUserDAO implements UserDAO {

    public void createUser(UserData userData) throws DataAccessException {
        var statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        executeUpdate(statement, userData);
    }

    public UserData getUser(String username) throws DataAccessException {
        return new UserData("not a username", "not a password", "not an email");
    }

    public void clear() {}

    private void executeUpdate(String statement, UserData userData) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, userData.username());
                ps.setString(2, userData.password());
                ps.setString(3, userData.email());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to update database");
        }
    }

}
