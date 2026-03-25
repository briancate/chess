package client;

import model.GameData;
import model.JoinRequest;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }

    @BeforeEach
    public void clearDatabase() {
        // clear the database
        try {
            facade.getClientCommunicator().clear();
        }
        catch (Exception ex) {
            System.out.println("Clearing somehow failed, I'm impressed");
        }
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void successfulRegister() {
        var result = facade.register(new UserData("brian", "password", "email"));
        assertTrue(result.authToken().length() > 10);
    }

    @Test
    public void unsuccessfulRegister() {
        // register a user
        facade.register(new UserData("brian", "password", "email"));
        // attempt to use the same username
        var result = facade.register(new UserData("brian", "new password", "new email"));
        assertNotNull(result.message());
    }

    @Test
    public void successfulLogin() {
        facade.register(new UserData("brian", "password", "email"));
        var result = facade.login(new UserData("brian", "password", null));
        assertTrue(result.authToken().length() > 10);
    }

    @Test
    public void unsuccessfulLogin() {
        facade.register(new UserData("brian", "password", "email"));
        var result = facade.login(new UserData("brian", "not my password", null));
        assertNotNull(result.message());
    }

    @Test
    public void successfulLogout() {
        facade.register(new UserData("brian", "password", "email"));
        var loginResult = facade.login(new UserData("brian", "password", null));
        var logoutResult = facade.logout(loginResult.authToken());
        assertNull(logoutResult); // if it was null, there would be no error message
    }

    @Test
    public void unsuccessfulLogout() {
        facade.register(new UserData("brian", "password", "email"));
        facade.login(new UserData("brian", "password", null));
        var logoutResult = facade.logout("not an authToken");
        assertNotNull(logoutResult); // if it was null, there would be no error message
    }

    @Test
    public void successfulCreateGame() {
        var registerResult = facade.register(new UserData("brian", "password", "email"));
        GameData gameData = new GameData(0, null, null, "Gamename!", null);
        var createResult = facade.createGame(gameData, registerResult.authToken());
        assertTrue(createResult.gameID() != 0); // so it actually updated the gameID
    }

    @Test
    public void unsuccessfulCreateGame() {
        var registerResult = facade.register(new UserData("brian", "password", "email"));
        GameData gameData = new GameData(0, null, null, null, null);
        var createResult = facade.createGame(gameData, registerResult.authToken());
        assertNotNull(createResult.message()); // assert there's an error message
    }

    @Test
    public void successfulListGames() {
        var registerResult = facade.register(new UserData("brian", "password", "email"));
        var listGamesResult = facade.listGames(registerResult.authToken());
        assertNotNull(listGamesResult.games()); // even if it's empty, it shouldn't be null
    }

    @Test
    public void unsuccessfulListGames() {
        facade.register(new UserData("brian", "password", "email"));
        var listGamesResult = facade.listGames("not a valid authToken");
        assertNotNull(listGamesResult.message()); // assert there's an error message
    }

    @Test
    public void successfulJoinGame() {
        var registerResult = facade.register(new UserData("brian", "password", "email"));
        GameData gameData = new GameData(0, null, null, "Gamename!", null);
        facade.createGame(gameData, registerResult.authToken());
        JoinRequest validRequest = new JoinRequest("WHITE", 1, registerResult.authToken());

        var joinResult = facade.joinGame(validRequest);
        assertNull(joinResult); // successful joinGame returns a null joinResult object
    }

    @Test
    public void unsuccessfulJoinGame() {
        var registerResult = facade.register(new UserData("brian", "password", "email"));
        GameData gameData = new GameData(0, null, null, "Gamename!", null);
        facade.createGame(gameData, registerResult.authToken());
        JoinRequest invalidRequest = new JoinRequest("WHITE", 42, registerResult.authToken());

        var joinResult = facade.joinGame(invalidRequest);
        assertNotNull(joinResult.message()); // assert there's not an error message
    }

}
