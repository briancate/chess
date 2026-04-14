package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import server.ResponseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLWsDAO {

    private final Gson gson = new Gson();

    public ChessGame getGame(int gameID) throws DataAccessException, ResponseException {
        var statement = "SELECT chessgame from games WHERE gameid = ?";
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

    private ChessGame readGame(ResultSet rs) throws SQLException {
        return gson.fromJson(rs.getString("chessgame"), ChessGame.class);
    }
}
