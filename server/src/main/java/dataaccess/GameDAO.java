package dataaccess;

import model.GameData;
import model.JoinData;

import java.util.Collection;

public interface GameDAO {

    int createGame(GameData gameData);

    GameData getGame(int gameID) throws DataAccessException;

    Collection<GameData> listGames();

    void updateWhiteUsername(JoinData joinData, String username);

    void updateBlackUsername(JoinData joinData, String username);


    void clear();
}
