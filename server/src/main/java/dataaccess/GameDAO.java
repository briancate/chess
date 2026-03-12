package dataaccess;

import model.GameData;
import model.JoinData;
import server.ResponseException;

import java.util.Collection;

public interface GameDAO {

    int createGame(GameData gameData) throws ResponseException;

    GameData getGame(int gameID) throws DataAccessException, ResponseException;

    Collection<GameData> listGames() throws ResponseException;

    void updateWhiteUsername(JoinData joinData, String username) throws ResponseException;

    void updateBlackUsername(JoinData joinData, String username) throws ResponseException;

    void clear() throws ResponseException;
}
