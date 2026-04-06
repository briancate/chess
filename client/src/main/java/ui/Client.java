package ui;

import model.JoinResult;
import client.ServerFacade;
import model.*;
import static ui.EscapeSequences.SET_TEXT_COLOR_WHITE;

import java.util.ArrayList;
import java.util.Scanner;

public class Client {

    private final ServerFacade serverFacade;
    private String authToken;
    private final Scanner scanner = new Scanner(System.in);


    public Client(int port) {
        serverFacade = new ServerFacade(port);
    }

    static void main() {
        Client client = new Client(8080); // I think this is right?
        client.run();
    }

    public void run() {
        System.out.print(SET_TEXT_COLOR_WHITE);
        System.out.println("Welcome to Brian's Fantastic 240 Chess Extravaganza!");
        printMenu();

        String input = "lol this doesn't matter";
        while (!input.equals("4")) {

            System.out.print("Please enter a number: ");
            input = scanner.nextLine();
            System.out.println();

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
        String input = "lol this doesn't matter";
        while (!input.equals("6")) {
            printMenu();
            System.out.print("Please enter a number: ");
            input = scanner.nextLine();
            System.out.println();

            switch (input) {
                case "1" -> listGames();
                case "2" -> createGame();
                case "3" -> joinGame();
                case "4" -> observeGame();
                case "5" -> help();
                case "6" -> {
                    logout();
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
                    Quit: exits the program""" + "\n");
        }
        else {
            System.out.println("""
                    List Games: lists all Chess games
                    Create Game: create a new Chess game
                    Play Game: join an existing Chess game as a player
                    Observe Game: join an existing Chess game as an observer
                    Help: displays this text
                    Logout: ends the session""" + "\n");
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

        resolveLoginAttempt(response);
    }

    private void login() {
        System.out.println("To login, please enter your username and password:");

        UserData userData = getUserDataFromUser("LOGIN");
        RegisterResponse response = serverFacade.login(userData);

        resolveLoginAttempt(response);
    }

    private void logout() {
        LogoutResponse response = serverFacade.logout(authToken);

        if (response == null) {System.out.println("Successfully logged out\n");}
        else {System.out.println("Logout request failed: " + response.message() + "\n");}

    }

    private void observeGame() {
        ListGamesResponse games = listGames();
        if (games.games().isEmpty()) {
            System.out.println("Unable to observe a game as no game have been created.");
            return;
        }

        ArrayList<Integer> validGameNumbers = new ArrayList<>();
        for (int i = 0; i < games.games().size(); i++) {
            validGameNumbers.add(i+1);
        }

        getValidGameNumber(validGameNumbers);

        // for the moment, just print out a chessboard
        // eventually though, I'll need to get the ChessGame to print it out
        System.out.println();
        ChessBoard.drawChessBoard("WHITE");
        System.out.println();
    }

    private void createGame() {
        System.out.print("Please enter a name for the game you wish to create: ");
        String gameName = scanner.nextLine();

        GameData gameData = new GameData(0, null, null, gameName, null);

        CreateResponse response = serverFacade.createGame(gameData, authToken);

        if (response.message() != null) {
            System.out.println("The create request failed: " + response.message() + "\n");
        }
        else {
            System.out.println("Successfully created game with name " + gameData.gameName() + "\n");
        }
    }

    private ListGamesResponse listGames() {
        // do a for loop through the games, use the index to go back and forth between gameId and gameNumber
        ListGamesResponse gamesResponse = serverFacade.listGames(authToken);

        if (gamesResponse.games() == null) {
            // this is probably unnecessary, I can't imagine how this could fail
            System.out.println("The list games request failed: " + gamesResponse.message() + "\n");
            return gamesResponse;
        }

        System.out.println("Here is a list of all games you can join:");
        ArrayList<JsonFriendlyGameData> gamesList = gamesResponse.games();
        for (int i = 0; i < gamesList.size(); i++) {
            JsonFriendlyGameData game = gamesList.get(i);
            System.out.println((i + 1) + ". Name: " + game.gameName() +
                    "\n   White Player: " + ((game.whiteUsername() == null) ? "unclaimed" : game.whiteUsername()) +
                    "\n   Black Player: " + ((game.blackUsername() == null) ? "unclaimed" : game.blackUsername()));
        }
        System.out.println();
        return gamesResponse;
    }

    private void joinGame() {
        // I'm going to list the games again just in case they added one since last listing them
        ListGamesResponse games = listGames();
        if (games.games().isEmpty()) {
            System.out.println("Unable to join a game as no games have been created.");
            return;
        }

        ArrayList<Integer> validGameNumbers = new ArrayList<>();
        for (int i = 0; i < games.games().size(); i++) {
            validGameNumbers.add(i+1);
        }

        Integer number = getValidGameNumber(validGameNumbers);
        String teamToJoin = getTeam();

        JoinRequest joinRequest = new JoinRequest(teamToJoin, games.games().get(number - 1).gameID(), authToken);

        JoinResult response =  serverFacade.joinGame(joinRequest);
        if (response != null) {
            System.out.println("The join request failed: " + response.message() + "\n");
            return;
        }

        System.out.println();
        if (teamToJoin.equals("WHITE")) {ChessBoard.drawChessBoard("WHITE");}
        else {ChessBoard.drawChessBoard("BLACK");}
        System.out.println();
    }


    private String getTeam() {
        System.out.println("Please enter the team you wish to join:");

        String input;
        while(true) {
            System.out.println("1: White");
            System.out.println("2: Black");
            input = scanner.nextLine();

            if (input.equals("1")) {return "WHITE";}
            else if (input.equals("2")) {return "BLACK";}
            else {System.out.println("Please enter either the number 1 or the number 2");}
        }
    }

    private Integer getValidGameNumber(ArrayList<Integer> validNumbers) {
        boolean gotValidInput = false;
        int gameNumber = -1;
        while (!gotValidInput) {
            if (gameNumber != -1) {System.out.print("Invalid number, please enter the number of one of the games listed. ");}
            else {System.out.print("Please enter the number of the game you wish to join: ");}

            try {
                gameNumber = Integer.parseInt(scanner.nextLine());
            }
            catch (Exception ex) {
                continue;
            }

            if (validNumbers.contains(gameNumber)) {gotValidInput = true;}
        }
        return gameNumber;
    }

    private UserData getUserDataFromUser(String requestType) {
        String username = getNonEmptyString("Please enter your username: ");
        String password = getNonEmptyString("Please enter your password: ");

        String email;
        if (requestType.equals("REGISTER")) {
            email = getNonEmptyString("Please enter your email: ");
        }
        else {email = null;}

        return new UserData(username, password, email);
    }

    private void resolveLoginAttempt(RegisterResponse response) {
        if (response.message() != null) {
            // find a way to give more information here
            System.out.println("The login request failed: " + response.message() + "\n");
        }
        else {
            authToken = response.authToken();
            System.out.println("Successfully logged in as " + response.username() + "!\n");
        }

        if (authToken != null) {loginREPL();}
    }

    private String getNonEmptyString(String prompt) {
        String output = "";
        int iteration = 0;
        while (output.isEmpty()) {
            if (iteration != 0) {System.out.println("Please enter a string with at least one character");}
            System.out.print(prompt);
            output = scanner.nextLine();
            iteration++;
        }
        return output;
    }

    // Have "back" options for join game, etc.?
    // Go over error handling again
    // Should play or observe stop the repl loop?
}
