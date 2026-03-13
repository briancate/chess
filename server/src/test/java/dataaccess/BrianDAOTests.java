package dataaccess;

import chess.ChessGame;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import model.*;
import server.ResponseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BrianDAOTests {

    private SQLAuthDAO authDAO;
    private SQLUserDAO userDAO;
    private SQLGameDAO gameDAO;

    @BeforeEach
    void setup() {
        // it would be better to separate tests so I only create one DAO in the before each
        authDAO = new SQLAuthDAO();
        userDAO = new SQLUserDAO();
        gameDAO = new SQLGameDAO();

        // clear the tables before running each test
        String[] createStatements = {"TRUNCATE auths", "TRUNCATE users", "TRUNCATE games"};
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
                try (PreparedStatement ps = conn.prepareStatement(statement)) {
                    ps.executeUpdate();
                }
            }
        }
        catch (SQLException | DataAccessException e) {
            throw new RuntimeException("Error: unable to clear tables");
        }

    }

    void tryValidSQLQuery(String statement, String parameter) throws DataAccessException, SQLException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, parameter);
                ResultSet rs = ps.executeQuery();
                if (!rs.next()) {throw new DataAccessException("Error: query returned an empty set");}
            }
        }
    }


    @Test
    void successfulCreateAuth() throws ResponseException {
        AuthData authData = new AuthData("abc123", "brian");
        authDAO.createAuth(authData);

        String statement = "SELECT authtoken, username from auths WHERE username = ?";
        Assertions.assertDoesNotThrow(() -> tryValidSQLQuery(statement, authData.username()));
    }

    @Test
    void unsuccessfulCreateAuth() {
        AuthData authData = new AuthData("abc123", null);
        Assertions.assertThrows(ResponseException.class, () -> authDAO.createAuth(authData));
    }

    @Test
    void successfulGetAuth() throws ResponseException, DataAccessException {
        AuthData authData = new AuthData("abc123", "brian");
        authDAO.createAuth(authData);
        authDAO.getAuth(authData.authToken());

        String statement = "SELECT authtoken, username from auths where authtoken = ?";
        Assertions.assertDoesNotThrow(() -> tryValidSQLQuery(statement, authData.authToken()));
    }

    @Test
    void unsuccessfulGetAuth() throws ResponseException {
        AuthData authData = new AuthData("abc123", "brian");
        authDAO.createAuth(authData);
        Assertions.assertThrows(DataAccessException.class, () -> authDAO.getAuth("not abc123"));
    }

    @Test
    void successfulDeleteAuth() throws ResponseException {
        AuthData authData = new AuthData("abc123", "brian");
        authDAO.createAuth(authData);
        authDAO.deleteAuth(authData.authToken());

        String statement = "SELECT authtoken, username from auths where authtoken = ?";
        Assertions.assertThrows(DataAccessException.class, () -> tryValidSQLQuery(statement, authData.authToken()));
    }

    @Test
    void unsuccessfulDeleteAuth() throws ResponseException {
        AuthData authData = new AuthData("abc123", "brian");
        authDAO.createAuth(authData);

        authDAO.deleteAuth(null);
        String statement = "SELECT authtoken, username from auths where authtoken = ?";
        Assertions.assertDoesNotThrow(() -> tryValidSQLQuery(statement, authData.authToken()));
    }

    @Test
    void clearAuth() throws ResponseException {
        AuthData authData = new AuthData("abc123", "brian");
        authDAO.createAuth(authData);
        authDAO.clear();

        String statement = "SELECT authtoken, username from auths where authtoken = ?";
        Assertions.assertThrows(DataAccessException.class, () -> tryValidSQLQuery(statement, authData.authToken()));
    }

    @Test
    void successfulCreateUser() throws ResponseException, DataAccessException {
        UserData userData = new UserData("brian", "mypw", "email");
        userDAO.createUser(userData);

        String statement = "SELECT username from users where username = ?";
        Assertions.assertDoesNotThrow(() -> tryValidSQLQuery(statement, userData.username()));
    }

    @Test
    void unsuccessfulCreateUser() {
        UserData userData = new UserData(null, "mypw", "email");
        Assertions.assertThrows(ResponseException.class, () -> userDAO.createUser(userData));


    }

    @Test
    void successfulGetUser() throws DataAccessException, ResponseException {
        UserData userData = new UserData("brian", "mypw", "email");
        userDAO.createUser(userData);

        Assertions.assertDoesNotThrow(() -> userDAO.getUser("brian"));
    }

    @Test
    void unsuccessfulGetUser() throws DataAccessException, ResponseException {
        UserData userData = new UserData("brian", "mypw", "email");
        userDAO.createUser(userData);

        Assertions.assertThrows(DataAccessException.class, () -> userDAO.getUser("not brian"));
    }

    @Test
    void clearUser() throws DataAccessException, ResponseException {
        UserData userData = new UserData("brian", "mypw", "email");
        userDAO.createUser(userData);
        userDAO.clear();

        String statement = "SELECT username from users where username = ?";
        Assertions.assertThrows(DataAccessException.class, () -> tryValidSQLQuery(statement, userData.username()));
    }

    @Test
    void successfulCreateGame() throws ResponseException {
        GameData gameData = new GameData(0, null, null, "the bestest game", new ChessGame());
        gameDAO.createGame(gameData);

        String statement = "SELECT gamename from games where gamename = ?";
        Assertions.assertDoesNotThrow(() -> tryValidSQLQuery(statement, gameData.gameName()));
    }

    @Test
    void unsuccessfulCreateGame() {
        GameData gameData = new GameData(0, null, null, null, new ChessGame());
        Assertions.assertThrows(ResponseException.class, () -> gameDAO.createGame(gameData));
    }

    @Test
    void successfulGetGame() throws DataAccessException, ResponseException {
        GameData gameData = new GameData(0, null, null, "the bestest game", new ChessGame());
        gameDAO.createGame(gameData);
        gameDAO.getGame(1);

        String statement = "SELECT gamename from games where gameid = ?";
        Assertions.assertDoesNotThrow(() -> tryValidSQLQuery(statement, "1"));
    }

    @Test
    void unsuccessfulGetGame() throws ResponseException {
        GameData gameData = new GameData(0, null, null, "the bestest game", new ChessGame());
        gameDAO.createGame(gameData);
        Assertions.assertThrows(DataAccessException.class, () -> gameDAO.getGame(42));
    }

    @Test
    void successfulListGames() throws ResponseException {
        GameData gameData = new GameData(0, null, null, "the bestest game", new ChessGame());
        gameDAO.createGame(gameData);
        gameDAO.listGames();

        String statement = "SELECT gamename from games where gameid = ?";
        Assertions.assertDoesNotThrow(() -> tryValidSQLQuery(statement, "1"));
    }

    @Test
    void successfulUpdateWhiteUsername() throws DataAccessException, ResponseException {
        GameData gameData = new GameData(0, null, null, "the bestest game", new ChessGame());
        gameDAO.createGame(gameData);
        gameDAO.updateWhiteUsername(new JoinData("white", 1), "brian");

        String statement = "SELECT whiteusername from games where gameid = ?";
        Assertions.assertDoesNotThrow(() -> tryValidSQLQuery(statement, "1"));

    }

    @Test
    void unsuccessfulUpdateWhiteUsername() throws DataAccessException, ResponseException {
        GameData gameData = new GameData(0, null, null, "the bestest game", new ChessGame());
        gameDAO.createGame(gameData);
        gameDAO.updateWhiteUsername(new JoinData("white", 1), "brian");

        Assertions.assertThrows(DataAccessException.class, () -> gameDAO.updateWhiteUsername(new JoinData("white", 1), "not brian"));
    }

    @Test
    void successfulUpdateBlackUsername() throws DataAccessException, ResponseException {
        GameData gameData = new GameData(0, null, null, "the bestest game", new ChessGame());
        gameDAO.createGame(gameData);
        gameDAO.updateBlackUsername(new JoinData("black", 1), "brian");

        String statement = "SELECT blackusername from games where gameid = ?";
        Assertions.assertDoesNotThrow(() -> tryValidSQLQuery(statement, "1"));
    }

    @Test
    void unsuccessfulUpdateBlackUsername() throws DataAccessException, ResponseException {
        GameData gameData = new GameData(0, null, null, "the bestest game", new ChessGame());
        gameDAO.createGame(gameData);
        gameDAO.updateBlackUsername(new JoinData("black", 1), "brian");

        Assertions.assertThrows(DataAccessException.class, () -> gameDAO.updateBlackUsername(new JoinData("black", 1), "not brian"));
    }

    @Test
    void clearGame() throws ResponseException {
        GameData gameData = new GameData(0, null, null, "the bestest game", new ChessGame());
        gameDAO.createGame(gameData);
        gameDAO.clear();

        String statement = "SELECT gameid from games where gameid = ?";
        Assertions.assertThrows(DataAccessException.class, () -> tryValidSQLQuery(statement, "1"));
    }
}
