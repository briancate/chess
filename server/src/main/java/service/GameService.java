package service;

import dataaccess.GameDAO;
import model.GameData;

import java.util.Collection;

public class GameService {

    private final GameDAO gameDAO;

    public GameService(GameDAO gameDAO) {this.gameDAO = gameDAO;}

    public int createGame(GameData gameData) {
        return gameDAO.createGame(gameData);
    }

    public Collection<GameData> listGames() {
        return gameDAO.listGames();
    }

}
