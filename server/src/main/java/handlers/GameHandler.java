package handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;
import model.JoinData;
import model.JsonFriendlyGameData;
import server.ResponseException;
import service.GameService;
import io.javalin.http.Context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class GameHandler {

    private final GameService gameService;
    private final AuthHandler authHandler;
    private final Gson gson = new Gson();

    public GameHandler(GameDAO gameDAO, AuthHandler authHandler) {
        this.gameService = new GameService(gameDAO);
        this.authHandler = authHandler;
    }

    public void handleCreate(Context ctx) throws DataAccessException, ResponseException {
        authHandler.validateAuth(ctx);

        GameData gameData = gson.fromJson(ctx.body(), GameData.class); // create a GameData (most will be null)
        if (gameData.gameName()==null || gameData.gameName().isEmpty()) { // if they didn't provide a name, throw a 400 error
            ctx.status(400);
            throw new DataAccessException("Error: bad request");
        }
        int gameID = gameService.createGame(gameData);
        ctx.result(gson.toJson(Map.of("gameID", gameID)));
    }

    public void listGames(Context ctx) throws DataAccessException, ResponseException {
        authHandler.validateAuth(ctx);
        Collection<GameData> oldGameList = gameService.listGames();
        ArrayList<JsonFriendlyGameData> gameList = new ArrayList<>();

        for (GameData game : oldGameList) {
            gameList.add(new JsonFriendlyGameData(
                    game.gameID(),
                    game.whiteUsername(),
                    game.blackUsername(),
                    game.gameName())
            );
        }
        ctx.result(gson.toJson(Map.of("games", gameList)));
    }

    public void handleJoin(Context ctx) throws DataAccessException, ResponseException {
        String authToken = ctx.header("authorization");
        authHandler.validateAuth(ctx);
        JoinData joinData = gson.fromJson(ctx.body(), JoinData.class);
        GameData gameData;
        try {
            gameData = gameService.getGame(joinData.gameID());
        }
        catch (DataAccessException ex) {
            ctx.status(400);
            throw ex;
        }
        if (joinData.playerColor()==null ||
                (!joinData.playerColor().equals("WHITE") && !joinData.playerColor().equals("BLACK"))) {
            ctx.status(400);
            throw new DataAccessException("Error: bad request");
        }

        AuthData authData = authHandler.getAuth(authToken);
        String username = authData.username();

        if ((gameData.whiteUsername()!=null && joinData.playerColor().equals("WHITE")) ||
                gameData.blackUsername()!=null && joinData.playerColor().equals("BLACK")) {
            ctx.status(403);
            throw new DataAccessException("Error: already taken");
        }
        gameService.updateGame(joinData, username);
    }
    public void clear(Context ctx) throws DataAccessException {
        try {
            gameService.clear();
        }
        catch (DataAccessException ex) {
            ctx.status(500);
            throw ex;
        }
    }

}
