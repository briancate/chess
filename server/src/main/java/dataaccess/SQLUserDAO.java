package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import server.ResponseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
//import

public class SQLUserDAO implements UserDAO {

    private void checkIfTaken(String username) throws DataAccessException, ResponseException {
        var statement = "SELECT username from users WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
//                    throw new DataAccessException("Error: user already exists with given username");
                    throw new Exception("Error: user already exists with given username");
                }
            }
        } catch (SQLException | DataAccessException e) {
            throw new ResponseException("Error: Unable to update database");
        }
        catch (Exception ex) {
            throw new DataAccessException("Error: " + ex.getMessage());
        }
    }

    public void createUser(UserData userData) throws ResponseException, DataAccessException {

        checkIfTaken(userData.username());

        var statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, userData.username());
//                ps.setString(2, userData.password());
                String hashedPassword = BCrypt.hashpw(userData.password(), BCrypt.gensalt());
                ps.setString(2, hashedPassword);
                ps.setString(3, userData.email());
                ps.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new ResponseException("Error: Database error: " + e.getMessage());
        }
    }

    private UserData readUser(ResultSet rs) throws SQLException {
        String username = rs.getString("username");
        String password = rs.getString("password");
        String email = rs.getString("email");
        return new UserData(username, password, email);

    }

    public UserData getUser(String username) throws DataAccessException, ResponseException {
        var statement = "SELECT username, password, email FROM users WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    return readUser(rs);
                }
            }
        } catch (SQLException | DataAccessException e) {
            throw new ResponseException("Error: Unable to update database");
        }
        throw new DataAccessException("Error: No user with the given username");
    }

    public void clear() throws ResponseException {
        var statement = "TRUNCATE users";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new ResponseException("Error: Unable to update database");
        }
    }
}
