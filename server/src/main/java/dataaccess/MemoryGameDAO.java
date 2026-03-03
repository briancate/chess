package dataaccess;

import chess.ChessGame;
import model.GameData;
import model.JoinData;

import java.util.ArrayList;
import java.util.Collection;

public class MemoryGameDAO implements GameDAO {

    ArrayList<GameData> gameList = new ArrayList<>();
    private int gameIDCounter = 1;

    // no constructor needed here, since it doesn't need any parameters (and thus super() works)

    public int createGame(GameData incompleteGameData) {
        GameData gameData = new GameData(gameIDCounter++, null, null, incompleteGameData.gameName(), new ChessGame());
        gameList.add(gameData);
        return gameData.gameID();
    }

    public GameData getGame(int gameID) throws DataAccessException {
        for (GameData game : gameList) {
            if (game.gameID() == gameID) {return game;}
        }
        throw new DataAccessException("Error: bad request");
    }

    public Collection<GameData> listGames() {return gameList;}

    public void updateWhiteUsername(JoinData joinData, String username) {
        for (GameData game : gameList) {
            if (game.gameID() == joinData.gameID()) {
                GameData newGameData = new GameData(game.gameID(), username, game.blackUsername(), game.gameName(), game.game());
                gameList.add(newGameData);
                gameList.remove(game);
                return;
            }
        }
    }

    public void updateBlackUsername(JoinData joinData, String username) {
        for (GameData game : gameList) {
            if (game.gameID() == joinData.gameID()) {
                GameData newGameData = new GameData(game.gameID(), game.whiteUsername(), username, game.gameName(), game.game());
                gameList.add(newGameData);
                gameList.remove(game);
                return;
            }
        }
    }

    public void clear() {
        gameList.clear();
    }
}
