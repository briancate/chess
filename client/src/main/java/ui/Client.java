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

            input = scanner.nextLine();

            switch (input) {
                case "1" -> {
                    System.out.println("This should register you");
                    System.out.println("Switching to 2nd REPL...");
                }
                case "2" -> {
                    System.out.println("This should log you in");
                    System.out.println("Switching to 2nd REPL...");
                }
                case "3" -> help();
                default -> System.out.println("Please enter a number between 1 and 4");
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
                    4. Quit
                    Please enter a number:""");
        }
        else {
            System.out.println("""
                    1. List Games
                    2. Create Game
                    3. Play Game
                    4. Observe Game
                    5. Help
                    6. Logout
                    Please enter a number:""");
        }

    }

}
