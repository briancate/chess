package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLUserDAO implements UserDAO {

    public void createUser(UserData userData) throws DataAccessException {
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
        } catch (SQLException e) {
            throw new DataAccessException("Error: Database error: " + e.getMessage());
        }
    }

    public UserData readUser(ResultSet rs) throws SQLException {
        String username = rs.getString("username");
        String password = rs.getString("password");
        String email = rs.getString("email");
        return new UserData(username, password, email);

    }

    public UserData getUser(String username) throws DataAccessException {
        var statement = "SELECT username, password, email FROM users WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    return readUser(rs);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: Unable to update database");
        }
        throw new DataAccessException("Error: No user with the given username");
    }

    public void clear() throws DataAccessException {
        var statement = "TRUNCATE users";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: Unable to update database");
        }
    }

//    private void executeUpdate(String statement, String username, String password, String email) throws DataAccessException {
//        try (Connection conn = DatabaseManager.getConnection()) {
//            try (PreparedStatement ps = conn.prepareStatement(statement)) {
//                ps.setString(1, username);
//                ps.setString(2, password);
//                ps.setString(3, email);
//                ps.executeUpdate();
//            }
//        } catch (SQLException e) {
//            throw new DataAccessException("Unable to update database");
//        }
//    }

}
