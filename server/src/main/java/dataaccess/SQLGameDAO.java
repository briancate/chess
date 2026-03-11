package dataaccess;

import chess.ChessGame;
import model.GameData;
import model.JoinData;

import java.util.ArrayList;
import java.util.Collection;

public class SQLGameDAO {

    public int createGame(GameData gameData) {return 0;}

    public GameData getGame(int gameID) throws DataAccessException {
        return new GameData(0, "white", "black", "not a name", new ChessGame());
    }

    public Collection<GameData> listGames() {
        return new ArrayList<GameData>();
    }

    public void updateWhiteUsername(JoinData joinData, String username) {}

    public void updateBlackUsername(JoinData joinData, String username) {}

    public void clear() {}

}
