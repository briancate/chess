package dataaccess;

import model.GameData;

import java.util.Collection;

public interface GameDAO {

    int createGame(GameData gameData);

    GameData getGame(String gameName) throws DataAccessException;

    Collection<GameData> listGames();

    void updateGame();
}
