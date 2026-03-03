package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;
import java.util.Collection;

public class MemoryGameDAO implements GameDAO {

    ArrayList<GameData> gameList = new ArrayList<>();
    private int gameIDCounter = 0;

    // no constructor needed here, since it doesn't need any parameters (and thus super() works)

    public int createGame(GameData incompleteGameData) {
        GameData gameData = new GameData(gameIDCounter++, null, null, incompleteGameData.gameName(), new ChessGame());
        gameList.add(gameData);
        return gameData.gameID();
    }

    public GameData getGame(String gameName) throws DataAccessException {
        for (GameData game : gameList) {
            if (game.gameName().equals(gameName)) {return game;}
        }
        throw new DataAccessException("Error: bad request");
    }

    public Collection<GameData> listGames() {return gameList;} // does this need to throw an error?

    public void updateGame() {
        // this will be more complicated since it has to deal with users joining the game
        // and handling updates to the game via people making moves, idk how to do that
    }
}
