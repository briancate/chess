package ui;

import client.ServerFacade;
import model.*;

import java.util.ArrayList;
import java.util.Scanner;

public class Client {

    private final ServerFacade serverFacade;
    private String authToken;
    private final Scanner scanner = new Scanner(System.in);


    public Client(String serverURL) {
        // flesh out the constructor
        serverFacade = new ServerFacade(serverURL);
    }

    static void main() {
        Client client = new Client("localhost:8080"); //idk what url I should be using tbh
        client.run();
    }

    public void run() {
        System.out.println("Welcome to Brian's Fantastic 240 Chess Extravaganza!");
        printMenu();

        String input = "lol this doesn't matter";
        while (!input.equals("4")) {

            System.out.print("Please enter a number:");
            input = scanner.nextLine();

            switch (input) {
                case "1" -> register();
                case "2" -> login();
                case "3" -> help();
                case "4" -> System.out.println("Thanks for playing!");
                default -> System.out.println("Your selection must be a number between 1 and 4");
            }
        }
    }


    public void loginREPL() {
        System.out.println("Login Successful!");
//        printMenu();

        String input = "lol this doesn't matter";
        while (!input.equals("6")) {

            printMenu();
            System.out.print("Please enter a number: ");
            input = scanner.nextLine();

            switch (input) {
                case "1" -> listGames();
                case "2" -> createGame();
                case "3" -> System.out.println("This should play a game");
                case "4" -> ChessBoard.drawChessBoard("WHITE"); // since for the moment, we can't actually observe a game
                case "5" -> help();
                case "6" -> {
                    authToken = null;
                    printMenu();
                }
                default -> System.out.println("Your selection must be a number between 1 and 6");
            }
        }

    }


    public void help() {
        if (authToken == null) {
            System.out.println("""
                    Register: create an account
                    Login: login to an existing account
                    Help: displays this text
                    Quit: exits the program""");
        }
        else {
            System.out.println("""
                    List Games: lists all Chess games
                    Create Game: create a new Chess game
                    Play Game: join an existing Chess game as a player
                    Observe Game: join an existing Chess game as an observer
                    Help: displays this text
                    Logout: ends the session""");
        }
    }

    public void printMenu() {
        if (authToken == null) {
            System.out.println("""
                    1. Register
                    2. Login
                    3. Help
                    4. Quit""");
        }
        else {
            System.out.println("""
                    1. List Games
                    2. Create Game
                    3. Play Game
                    4. Observe Game
                    5. Help
                    6. Logout""");
        }
    }

    private void register() {
        System.out.println("To register, please fill out the following fields:");

        UserData userData = getUserDataFromUser("REGISTER");
        RegisterResponse response = serverFacade.register(userData);

        // start the login loop if the register attempt was successful
        resolveLoginAttempt(response);
    }

    private void login() {
        System.out.println("To login, please enter your username and password:");

        UserData userData = getUserDataFromUser("LOGIN");
        RegisterResponse response = serverFacade.login(userData);

        // start the login loop if the register attempt was successful
        resolveLoginAttempt(response);
    }


    private void createGame() {
        System.out.print("Please enter a name for the game you wish to create: ");
        String gameName = scanner.nextLine();

        GameData gameData = new GameData(0, null, null, gameName, null);

        CreateResponse response = serverFacade.createGame(gameData, authToken);

        if (response.message() != null) {
            System.out.println("The create request failed: " + response.message());
        }
        else {
            System.out.println("Successfully created game with name " + gameData.gameName());
        }
    }

    private void listGames() {
        // wait this needs me to take care of separating the gameIDs from the numbers I show the user...
        // keep track of gameIDs here

        // do a for loop through the ids, use the index to go back and forth
        ListGamesResponse gamesResponse = serverFacade.listGames(authToken);

        if (gamesResponse.games() == null) {
            // this is probably unnecessary, I can't imagine how this could fail
            System.out.println("The list games request failed: " + gamesResponse.message());
            return;
        }

        System.out.println("Here is a list of all games you can join:");
        ArrayList<JsonFriendlyGameData> gamesList = gamesResponse.games();
        for (int i = 0; i < gamesList.size(); i++) {
            JsonFriendlyGameData game = gamesList.get(i);
            System.out.println((i + 1) + ". Name: " + game.gameName() + "\n   White Player: " +
                    game.whiteUsername() + "\n   Black Player: " + game.blackUsername());
        }
    }


    private UserData getUserDataFromUser(String requestType) {
        System.out.print("Please enter your username: ");
        String username = scanner.nextLine();
        System.out.print("Please enter your password: ");
        String password = scanner.nextLine();

        String email;
        if (requestType.equals("REGISTER")) {
            System.out.print("Please enter your email: ");
            email = scanner.nextLine();
        }
        else {email = null;}

        return new UserData(username, password, email);
    }

    private void resolveLoginAttempt(RegisterResponse response) {
        if (response.message() != null) {
            // find a way to give more information here
            System.out.println("The login request failed: " + response.message());
        }
        else {
            authToken = response.authToken();
            // this should handle the repl loops
            // and then create request objects to pass to the Server Facade
            System.out.println("Successfully logged in as " + response.username());
        }

        if (authToken == null) {run();}
        else {loginREPL();}
    }

    // EMPTY STRINGS DON'T THROW ERRORS... should they? Probably?
    // Potentially having to quit twice? I only ran into that bug once...

}
