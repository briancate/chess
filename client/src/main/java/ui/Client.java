package ui;

import client.ServerFacade;

import java.util.Scanner;

public class Client {

    // this should handle the repl loops
    // and then create request objects to pass to the Server Facade
    private String visitor;
    private ServerFacade serverFacade;
    private String authToken;

    public Client() {
        // flesh out the constructor
//        serverFacade = new ServerFacade();
    }

    static void main() {
        Client client = new Client();
        client.run();
    }

    public void run() {
        System.out.println("Welcome to Brian's Fantastic 240 Chess Extravaganza!");
        printMenu();

        Scanner scanner = new Scanner(System.in);
        String input = "lol this doesn't matter";
        while (!input.equals("4")) {

            System.out.println("Please enter a number:");
            input = scanner.nextLine();

            switch (input) {
                case "1" -> {
                    System.out.println("This should register you");
                    authToken = "not an authToken"; // THIS SHOULD BE THE RESULT OF A CLIENT COMMUNICATOR METHOD CALL
                    loginREPL();
                }
                case "2" -> {
                    System.out.println("This should log you in");
                    authToken = "not an authToken"; // THIS SHOULD BE THE RESULT OF A CLIENT COMMUNICATOR METHOD CALL
                    loginREPL();
                }
                case "3" -> help();
                default -> System.out.println("Your selection must be a number between 1 and 4");
            }
        }
    }


    public void loginREPL() {
        System.out.println("Login Successful!");
        printMenu();

        Scanner scanner = new Scanner(System.in);

        String input = "lol this doesn't matter";
        while (!input.equals("6")) {

            System.out.println("Please enter a number:");
            input = scanner.nextLine();

            switch (input) {
                case "1" -> System.out.println("This should list all games");
                case "2" -> System.out.println("This should create a game");
                case "3" -> System.out.println("This should play a game");
                case "4" -> System.out.println("This should observe a game");
                case "5" -> help();
                case "6" -> {
//                    System.out.println("This should log you out");
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
}
