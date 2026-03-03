package service;

import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;
import model.JoinData;

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

    public GameData getGame(int gameID) throws DataAccessException {
        return gameDAO.getGame(gameID);
    }

    public void updateGame(JoinData joinData, String username) throws DataAccessException {
        switch (joinData.playerColor()) {
            case "WHITE" : gameDAO.updateWhiteUsername(joinData, username); break;
            case "BLACK" : gameDAO.updateBlackUsername(joinData, username); break;
            default : throw new DataAccessException("Error: bad request");
        }
    }

    public void clear() {
        gameDAO.clear();
    }

}
