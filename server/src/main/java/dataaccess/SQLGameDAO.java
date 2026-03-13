package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import model.JoinData;
import server.ResponseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class SQLGameDAO implements GameDAO {

    private final Gson gson = new Gson();

    public int createGame(GameData gameData) throws ResponseException {
        var statement = "INSERT INTO games (gamename, chessgame) VALUES (?, ?)";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                ps.setString(1, gameData.gameName());
                ps.setString(2, gson.toJson(new ChessGame()));
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException | DataAccessException e) {
            throw new ResponseException("Error: Unable to update database");
        }
        throw new ResponseException("Error: did not return an integer");
    }

    private GameData readGame(ResultSet rs) throws SQLException {
        int gameID = rs.getInt("gameid");
        String whiteUsername = rs.getString("whiteusername");
        String blackUsername = rs.getString("blackusername");
        String gameName = rs.getString("gamename");
        ChessGame chessGame = gson.fromJson(rs.getString("chessgame"), ChessGame.class);
        return new GameData(gameID, whiteUsername, blackUsername, gameName, chessGame);
    }

    public GameData getGame(int gameID) throws DataAccessException, ResponseException {
        var statement = "SELECT gameid, whiteusername, blackusername, gamename, chessgame from games WHERE gameid = ?";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    return readGame(rs);
                }
            }
        } catch (SQLException e) {
            throw new ResponseException("Error: Unable to update database");
        }
        throw new DataAccessException("Error: no gameData with given gameID");
    }

    public Collection<GameData> listGames() throws ResponseException {
        var statement = "SELECT gameid, whiteusername, blackusername, gamename, chessgame from games";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ResultSet rs = ps.executeQuery();
                ArrayList<GameData> gameList = new ArrayList<>();
                while (rs.next()) {
                    gameList.add(readGame(rs));
                }
                return gameList;
            }
        } catch (SQLException | DataAccessException e) {
            throw new ResponseException("Error: Unable to update database");
        }

    }

    private void isTaken(String color, int gameID) throws DataAccessException {
        String teamUsername = color + "username";
        var statement = "SELECT ? from games where gameid = ? AND ? IS NOT NULL";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, teamUsername);
                ps.setInt(2, gameID);
                ps.setString(3, teamUsername);
                ResultSet rs = ps.executeQuery();
                if ((rs.getFetchSize() != 0)) {throw new DataAccessException( "Error: the username is already taken");}
            }
        }
        catch (SQLException ex) {
            throw new DataAccessException("Error: " + ex.getMessage());
        }
    }

    public void updateWhiteUsername(JoinData joinData, String username) throws DataAccessException, ResponseException {
        isTaken("white", joinData.gameID());

        var statement = "UPDATE games SET whiteusername = ? WHERE gameid = ?";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                ps.setInt(2, joinData.gameID());
                ps.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new ResponseException("Error: Unable to update database");
        }
    }

    public void updateBlackUsername(JoinData joinData, String username) throws DataAccessException, ResponseException {
        isTaken("black", joinData.gameID());

        var statement = "UPDATE games SET blackusername = ? WHERE gameid = ?";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                ps.setInt(2, joinData.gameID());
                ps.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new ResponseException("Error: Unable to update database");
        }
    }

    public void clear() throws ResponseException {
        var statement = "TRUNCATE games";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new ResponseException("Error: Unable to update database");
        }
    }

}
