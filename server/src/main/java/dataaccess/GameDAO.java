package dataaccess;

import model.GameData;
import model.JoinData;

import java.util.Collection;

public interface GameDAO {

    int createGame(GameData gameData) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    Collection<GameData> listGames() throws DataAccessException;

    void updateWhiteUsername(JoinData joinData, String username) throws DataAccessException;

    void updateBlackUsername(JoinData joinData, String username) throws DataAccessException;

    void clear() throws DataAccessException;
}
