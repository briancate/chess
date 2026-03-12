package dataaccess;

import model.AuthData;
import server.ResponseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLAuthDAO implements AuthDAO {

    public void createAuth(AuthData authData) throws DataAccessException { // throws ResponseException
        var statement = "INSERT INTO auths (authtoken, username) VALUES (?, ?)";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, authData.authToken());
                ps.setString(2, authData.username());
                ps.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException("Unable to update database");
        }
//        executeUpdate(statement, authData, json);
    }

    public AuthData readAuth(ResultSet rs) throws SQLException {
        String authToken = rs.getString("authtoken");
        String username = rs.getString("username");
        return new AuthData(authToken, username);

    }

    public AuthData getAuth(String authToken) throws DataAccessException {
        var statement = "SELECT authtoken, username from auths WHERE authtoken = ?";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    return readAuth(rs);
                }
            }
        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException("Unable to update database");
        }
        throw new DataAccessException("No authData exists with the given authToken");
    }

    public void deleteAuth(String authToken) throws DataAccessException {
        var statement = "DELETE from auths WHERE authtoken = ?";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                ps.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException("Unable to update database");
        }
        throw new DataAccessException("No authData exists with the given authToken");

    }

    public void clear() {

    }


}
