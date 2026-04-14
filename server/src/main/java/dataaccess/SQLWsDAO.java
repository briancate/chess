package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import server.ResponseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLWsDAO {

    private final Gson gson = new Gson();

    public GameData getGame(int gameID) throws DataAccessException, ResponseException {
        var statement = "SELECT * from games WHERE gameid = ?";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    return readGame(rs);
                }
            }
        } catch (SQLException | DataAccessException e) {
            throw new ResponseException("Error: Unable to update database");
        }
        throw new DataAccessException("Error: no gameData with given gameID");
    }

    private GameData readGame(ResultSet rs) throws SQLException {
        int gameID = rs.getInt("gameid");
        String whiteUsername = rs.getString("whiteusername");
        String blackUsername = rs.getString("blackusername");
        String gameName = rs.getString("gamename");
        ChessGame chessGame = gson.fromJson(rs.getString("chessgame"), ChessGame.class);
        return new GameData(gameID, whiteUsername, blackUsername, gameName, chessGame);
    }

    public void updateGame(int gameID, ChessGame game) throws Exception {
        var statement = "UPDATE games SET chessgame = ? WHERE gameid = ?";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, gson.toJson(game));
                ps.setInt(2, gameID);
                ps.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new ResponseException("Error: Unable to update database");
        }
    }

    public void removeUser(int gameID, String username) throws Exception {
        var statement = "UPDATE games SET " +
                "whiteusername = CASE WHEN whiteusername = ? THEN NULL ELSE whiteusername END," +
                "blackusername = CASE WHEN blackusername = ? THEN NULL ELSE blackusername END" +
                " WHERE gameid = ? AND (whiteusername = ? OR blackusername = ?)";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                ps.setString(2, username);
                ps.setInt(3, gameID);
                ps.setString(4, username);
                ps.setString(5, username);
                ps.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new ResponseException("Error: Unable to update database" + e.getMessage());
        }
    }
}
