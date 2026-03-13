package service;

import dataaccess.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import model.*;
import server.ResponseException;

import java.util.Collection;

public class BrianServiceTests {

    @Test
    void successfulRegister() throws ResponseException, DataAccessException {
        UserService userService = new UserService(new MemoryUserDAO());
        UserData userData = new UserData("brian", "abc123", "me@mail.com");
        userService.register(userData);
    }

    @Test
    void duplicateUserRegister() throws ResponseException, DataAccessException {
        UserService userService = new UserService(new MemoryUserDAO());
        UserData userData1 = new UserData("brian", "abc123", "me@mail.com");
        userService.register(userData1);
        UserData userData2 = new UserData("brian", "newpw", "new@gmail.com");
        Assertions.assertThrows(ResponseException.class, () -> userService.register(userData2));
    }

    @Test
    void successfulGetUser() throws DataAccessException, ResponseException {
        UserService userService = new UserService(new MemoryUserDAO());
        UserData userData1 = new UserData("brian", "abc123", "me@mail.com");
        userService.register(userData1);
        userService.getUser("brian");
    }

    @Test
    void unsuccessfulGetUser() throws ResponseException, DataAccessException {
        UserService userService = new UserService(new MemoryUserDAO());
        UserData userData1 = new UserData("brian", "abc123", "me@mail.com");
        userService.register(userData1);
        Assertions.assertThrows(DataAccessException.class, () -> userService.getUser("notbrian"));
    }

    @Test
    void clearUser() throws ResponseException, DataAccessException {
        UserService userService = new UserService(new MemoryUserDAO());
        UserData userData1 = new UserData("brian", "abc123", "me@mail.com");
        userService.register(userData1);
        userService.clear();
        Assertions.assertThrows(DataAccessException.class, () -> userService.getUser("brian"));
    }

    @Test
    void generateToken() {
        String token = AuthService.generateToken();
        Assertions.assertNotEquals("", token);
    }

    @Test
    void createAuth() throws ResponseException {
        AuthService authService = new AuthService(new MemoryAuthDAO());
        AuthData authData = new AuthData(AuthService.generateToken(), "brian");
        authService.createAuth(authData);
    }

    @Test
    void successfulGetAuth() throws DataAccessException, ResponseException {
        AuthService authService = new AuthService(new MemoryAuthDAO());
        AuthData authData = new AuthData(AuthService.generateToken(), "brian");
        authService.createAuth(authData);
        authService.getAuth(authData.authToken());
    }

    @Test
    void unsuccessfulGetAuth() throws ResponseException {
        AuthService authService = new AuthService(new MemoryAuthDAO());
        AuthData authData = new AuthData(AuthService.generateToken(), "brian");
        authService.createAuth(authData);
        Assertions.assertThrows(DataAccessException.class, () -> authService.getAuth("not an authToken"));
    }

    @Test
    void successfulDeleteAuth() throws ResponseException {
        AuthService authService = new AuthService(new MemoryAuthDAO());
        AuthData authData = new AuthData(AuthService.generateToken(), "brian");
        authService.createAuth(authData);
        authService.deleteAuth(authData.authToken());
    }

    @Test
    void unsuccessfulDeleteAuth() throws ResponseException {
        AuthService authService = new AuthService(new MemoryAuthDAO());
        AuthData authData = new AuthData(AuthService.generateToken(), "brian");
        authService.createAuth(authData);
        Assertions.assertThrows(ResponseException.class, () -> authService.deleteAuth("not an authToken"));
    }

    @Test
    void clearAuth() throws ResponseException {
        AuthService authService = new AuthService(new MemoryAuthDAO());
        AuthData authData = new AuthData(AuthService.generateToken(), "brian");
        authService.createAuth(authData);
        authService.clear();
        Assertions.assertThrows(ResponseException.class, () -> authService.deleteAuth(authData.authToken()));
    }

    @Test
    void successfulCreateGame() throws ResponseException {
        GameService gameService = new GameService(new MemoryGameDAO());
        GameData gameData = new GameData(0, null, null, "gamename", null);
        int gameID = gameService.createGame(gameData);
        Assertions.assertEquals(1, gameID);
    }

    @Test
    void successfulListGames() throws ResponseException {
        GameService gameService = new GameService(new MemoryGameDAO());
        GameData gameData = new GameData(0, null, null, "gamename", null);
        gameService.createGame(gameData);
        Collection<GameData> list = gameService.listGames();
        Assertions.assertEquals(1, list.size());
    }

    @Test
    void successfulGetGame() throws DataAccessException, ResponseException {
        GameService gameService = new GameService(new MemoryGameDAO());
        GameData gameData = new GameData(0, null, null, "gamename", null);
        int gameID = gameService.createGame(gameData);
        GameData retrievedData = gameService.getGame(gameID);
        Assertions.assertEquals(gameData.gameName(), retrievedData.gameName());
    }

    @Test
    void unsuccessfulGetGame() throws ResponseException {
        GameService gameService = new GameService(new MemoryGameDAO());
        GameData gameData = new GameData(0, null, null, "gamename", null);
        gameService.createGame(gameData);
        Assertions.assertThrows(DataAccessException.class, () -> gameService.getGame(45));
    }

    @Test
    void successfulUpdateGameWhite() throws DataAccessException, ResponseException {
        GameService gameService = new GameService(new MemoryGameDAO());
        GameData gameData = new GameData(0, null, null, "gamename", null);
        int gameID = gameService.createGame(gameData);
        gameService.updateGame(new JoinData("WHITE", gameID), "brian");
        GameData updatedGame = gameService.getGame(gameID);
        Assertions.assertEquals("brian", updatedGame.whiteUsername());
    }

    @Test
    void successfulUpdateGameBlack() throws DataAccessException, ResponseException {
        GameService gameService = new GameService(new MemoryGameDAO());
        GameData gameData = new GameData(0, null, null, "gamename", null);
        int gameID = gameService.createGame(gameData);
        gameService.updateGame(new JoinData("BLACK", gameID), "brian");
        GameData updatedGame = gameService.getGame(gameID);
        Assertions.assertEquals("brian", updatedGame.blackUsername());
    }

    @Test
    void unsuccessfulUpdateGame() throws ResponseException {
        GameService gameService = new GameService(new MemoryGameDAO());
        GameData gameData = new GameData(0, null, null, "gamename", null);
        int gameID = gameService.createGame(gameData);
        Assertions.assertThrows(DataAccessException.class, () -> gameService.updateGame(new JoinData("Salmon", gameID), "brian"));
    }

    @Test
    void clearGame() throws ResponseException {
        GameService gameService = new GameService(new MemoryGameDAO());
        GameData gameData = new GameData(0, null, null, "gamename", null);
        int gameID = gameService.createGame(gameData);
        gameService.clear();
        Assertions.assertThrows(DataAccessException.class, () -> gameService.getGame(gameID));
    }
}