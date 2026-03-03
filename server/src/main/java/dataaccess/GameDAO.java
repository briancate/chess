package dataaccess;

import model.GameData;

import java.util.Collection;

public interface GameDAO {

    public int createGame(GameData gameData);

    public GameData getGame(String gameName) throws DataAccessException;

    public Collection<GameData> listGames();

    public void updateGame();
}
