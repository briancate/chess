package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import client.ServerMessageObserver;
import com.google.gson.Gson;
import model.JoinResult;
import client.ServerFacade;
import model.*;
import websocket.commands.ConnectCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.LoadGame;
import websocket.messages.Notification;

import static ui.EscapeSequences.SET_TEXT_COLOR_WHITE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

public class Client implements ServerMessageObserver {

    private final ServerFacade serverFacade;
    private String authToken;
    private final Scanner scanner = new Scanner(System.in);
    private final Gson gson = new Gson();
    private ChessGame currentGame;


    public Client(int port) {
        serverFacade = new ServerFacade(port, this);
    }

    static void main() {
        Client client = new Client(8080); // I think this is right?
        client.run();
    }

    // Client will eventually need to implement ServerMessageObserver
    // have a method notify with a switch statement for notifications, errors, and load game

    public void displayNotification(Notification notification) {
        System.out.println("\n" + notification.getMessage() + "\n");
    }

    public void displayLoadGame(LoadGame loadGame) {
        currentGame = loadGame.getGame();
        System.out.println();
        ui.ChessBoard.drawChessBoard(loadGame.getTeamColor(), loadGame.getGame().getBoard(), ChessBoard.EMPTY_BOOLEAN_BOARD);
        System.out.println();
    }

    public void run() {
        System.out.print(SET_TEXT_COLOR_WHITE);
        System.out.println("Welcome to Brian's Fantastic 240 Chess Extravaganza!");
        printMenu(false);

        String input = "lol this doesn't matter";
        while (!input.equals("4")) {

            System.out.print("Please enter a number: ");
            input = scanner.nextLine();
            System.out.println();

            switch (input) {
                case "1" -> register();
                case "2" -> login();
                case "3" -> help(false);
                case "4" -> System.out.println("Thanks for playing!");
                default -> System.out.println("Your selection must be a number between 1 and 4");
            }
        }
    }

    public void postLoginREPL() {
        String input = "lol this doesn't matter";
        while (!input.equals("6")) {
            printMenu(false);
            System.out.print("Please enter a number: ");
            input = scanner.nextLine();
            System.out.println();

            switch (input) {
                case "1" -> listGames();
                case "2" -> createGame();
                case "3" -> joinGame();
                case "4" -> observeGame();
                case "5" -> help(false);
                case "6" -> {
                    logout();
                    authToken = null;
                    printMenu(false);
                }
                default -> System.out.println("Your selection must be a number between 1 and 6");
            }
        }
    }

    public void gameplayREPL(boolean isPlayer, String teamColor) {
        // do a load game request to get the board
        String input = "again, doesn't matter";
        while (!input.equals("6")) {
            printMenu(true);
            System.out.println("Please enter a number: ");
            input = scanner.nextLine();
            System.out.println();

            switch (input) {
                case "1" -> ui.ChessBoard.drawChessBoard(teamColor, currentGame.getBoard(), ChessBoard.EMPTY_BOOLEAN_BOARD);
                case "2" -> {
                    if (!isPlayer) {System.out.println("Unable to make moves as an observer");}
                    else {
                        ChessMove move = getChessMoveFromUser();
                        System.out.println("This is the move from the user:");
                        System.out.print(move + "\n");

                        Collection<ChessMove> validMoves = currentGame.validMoves(move.getStartPosition());

                        System.out.println("These are the valid moves");
                        for (ChessMove move2 : validMoves) {
                            System.out.println("User promotion: " + move.getPromotionPiece());
                            System.out.println("Valid promotion: " + move2.getPromotionPiece());
                        }

                        if (!validMoves.contains(move)) {System.out.println("Invalid move.");}
                        else {
                            System.out.println("This should make the move");
                        } // call the ws endpoint
                    }
                }
                case "3" -> {
                    ChessPosition position = getChessPositionFromUser("Please enter the position of the piece you wish to highlight:");
                    Collection<ChessPosition> positions = currentGame.findEndPositionsFromPiecePosition(position);
                    boolean [][] booleanBoard = ui.ChessBoard.generateFilledBooleanBoardFromPositions(positions);
                    ui.ChessBoard.drawChessBoard(teamColor, currentGame.getBoard(), booleanBoard);
                }
                case "4" -> help(true);
                case "5" -> {
                    if (!isPlayer) {System.out.println("Unable to resign as an observer");}
                    else {System.out.println("This should resign the game");}
                }
                case "6" -> {
                    if (isPlayer) {System.out.println("This should call a WS endpoint to update the game stored in the DB");}
                    System.out.println("This should leave the game");
                }
                default -> System.out.println("Your selection must be a number between 1 and 6");
            }
        }
    }


    public void help(boolean gameplayUI) {
        if (!gameplayUI) {
            if (authToken == null) {
                // for preLogin
                System.out.println("""
                        Register: create an account
                        Login: login to an existing account
                        Help: displays this text
                        Quit: exits the program""" + "\n");
            } else {
                // for postLogin
                System.out.println("""
                        List Games: lists all Chess games
                        Create Game: create a new Chess game
                        Play Game: join an existing Chess game as a player
                        Observe Game: join an existing Chess game as an observer
                        Help: displays this text
                        Logout: ends the session""" + "\n");
            }
        }
        else {
            // for gameplay UI
            System.out.println("""
                    Redraw Chess Board: draws the board again
                    Make Move: make a move in the current game
                    Highlight Legal Moves: show the legal moves of the piece at a give position
                    Help: displays this text
                    Resign: admit defeat
                    Leave: leave the current game""" + "\n");
        }
    }

    public void printMenu(boolean gameplayUI) {
        if (!gameplayUI) {
            if (authToken == null) {
                System.out.println("""
                        1. Register
                        2. Login
                        3. Help
                        4. Quit""");
            } else {
                System.out.println("""
                        1. List Games
                        2. Create Game
                        3. Play Game
                        4. Observe Game
                        5. Help
                        6. Logout""");
            }
        }
        else {
            System.out.println("""
                    1. Redraw Chess Board
                    2. Make Move
                    3. Highlight Legal Moves
                    4. Help
                    5. Resign
                    6. Leave""");
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
        for (int i = 0; i < games.games().size(); i++) {validGameNumbers.add(i+1);}

        Integer number = getValidGameNumber(validGameNumbers);
        String teamToJoin = getTeam();

        int gameID = games.games().get(number - 1).gameID();
        JoinRequest joinRequest = new JoinRequest(teamToJoin, gameID, authToken);

        JoinResult response =  serverFacade.joinGame(joinRequest);
        if (response != null) {
            System.out.println("The join request failed: " + response.message() + "\n");
            return;
        }

        connectWSToServer(gameID, teamToJoin);

        gameplayREPL(true, teamToJoin);

        // all of that was just to call the server endpoint
        // then call the server's /ws endpoint
        // send a connect ws message
        // transition to gameplay UI
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

        Integer number = getValidGameNumber(validGameNumbers);
        int gameID = games.games().get(number - 1).gameID();

        connectWSToServer(gameID, "an observer");

        // then call the server's /ws endpoint
        // send a connect ws message
        // transition to gameplay UI
        gameplayREPL(false, "WHITE");
    }

    private void connectWSToServer(int gameID, String teamColor) {
        ConnectCommand connectRequest = new ConnectCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID, teamColor);
        try {
            // eventually change this so the ServerFacade has a method for this instead of bypassing the SF completely
            serverFacade.getWebSocketCommunicator().send(gson.toJson(connectRequest));
        }
        catch (Exception e) {
            System.out.println("Error: " +  e.getMessage());
        }
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

        if (authToken != null) {postLoginREPL();}
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

    private ChessMove getChessMoveFromUser() {
        ChessPosition startPosition = getChessPositionFromUser("Please enter the starting position of the piece:");
        ChessPosition endPosition = getChessPositionFromUser("Please enter the ending position of the piece:");
        ChessPiece piece = currentGame.getBoard().getPiece(startPosition);
        ChessPiece.PieceType type = getPieceType(piece, endPosition);
        return new ChessMove(startPosition, endPosition, type);
    }

    private ChessPiece.PieceType getPieceType(ChessPiece piece, ChessPosition endPosition) {
        ChessPiece.PieceType type = piece.getPieceType();
        if (type == ChessPiece.PieceType.PAWN) {
            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE && endPosition.getRow() == 8) {
                type = getPromotionPieceFromUser();
            }
            else if (piece.getTeamColor() == ChessGame.TeamColor.BLACK && endPosition.getColumn() == 1) {
                type = getPromotionPieceFromUser();
            }
            else {type = null;}
        }
        else {type = null;}
        return type;
    }

    private ChessPiece.PieceType getPromotionPieceFromUser() {
        System.out.println("Please enter which piece you wish to promote your pawn to!");
        System.out.println("r = rook, n = knight, b = bishop, and q = queen");
        ArrayList<String> validInputs = new ArrayList<>(List.of("r", "n", "b", "q"));
        boolean gotValidInput = false;
        String output = "";
        while (!gotValidInput) {
            output = scanner.nextLine();
            if (validInputs.contains(output)) {gotValidInput = true;}
            else {System.out.println("Please enter 'r', 'n', 'b', or 'q'");}
        }
        switch (output) {
            case "r" -> {return ChessPiece.PieceType.ROOK;}
            case "n" -> {return ChessPiece.PieceType.KNIGHT;}
            case "b" -> {return ChessPiece.PieceType.BISHOP;}
            case "q" -> {return ChessPiece.PieceType.QUEEN;}
            default -> throw new RuntimeException("Why would you do this to me?");
        }
    }

    private ChessPosition getChessPositionFromUser(String prompt) { //
//        System.out.println("Please enter the position of the piece using the column letter and row number (ex e 4)");
        System.out.println(prompt);
        int col = getColFromUser();
        int row = getRowFromUser();
        return new ChessPosition(row, col);
    }

    private int getColFromUser() {
        System.out.println("Please enter the column of the piece (a lowercase letter between 'a' and 'h'): ");
        boolean gotValidInput = false;
        ArrayList<String> validInputs = new ArrayList<>(List.of("a", "b", "c", "d", "e", "f", "g", "h"));
        String output = "";
        while (!gotValidInput) {
            output = scanner.nextLine();
            if (validInputs.contains(output)) {gotValidInput = true;}
            else {System.out.println("Please enter a lowercase letter between 'a' and 'h'");}
        }
        return validInputs.indexOf(output) + 1;
    }

    private int getRowFromUser() {
        System.out.println("Please enter the row of the piece (an integer between 1 and 8): ");
        boolean gotValidInput = false;
        ArrayList<String> validInputs = new ArrayList<>(List.of("1", "2", "3", "4", "5", "6", "7", "8"));
        String output = "";
        while (!gotValidInput) {
            output = scanner.nextLine();
            if (validInputs.contains(output)) {gotValidInput = true;}
            else {System.out.println("Please enter an integer between 1 and 8");}
        }
        return Integer.parseInt(output);
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
