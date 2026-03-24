package ui;

import static ui.EscapeSequences.*;

public class ChessBoard {

    // Board dimensions
    // should I have a square or border width variables? Not unless I want to change squares to 3x3 instead of 1x1
    private static final int BOARD_SIZE_IN_SQUARES = 8;

    // character options when printing a square
    // I'll probably want one for "empty" and "pawn" later
    private static final String ROOK = "R";
    private static final String KNIGHT = "N";
    private static final String BISHOP = "B";
    private static final String QUEEN = "Q";
    private static final String KING = "K";

    // for each row, excluding border
    private static final String[] ROW_ONE_OR_EIGHT_WHITE_PERSPECTIVE = {ROOK, KNIGHT, BISHOP, QUEEN, KING, BISHOP, KNIGHT, ROOK};
    private static final String[] ROW_ONE_OR_EIGHT_BLACK_PERSPECTIVE = {ROOK, KNIGHT, BISHOP, KING, QUEEN, BISHOP, KNIGHT, ROOK};

    // for the borders, horizontal and vertical
    private static final String[] BORDER_ROW_WHITE_PERSPECTIVE = {"a", "b", "c", "d", "e", "f", "g", "h"};
    private static final String[] BORDER_ROW_BLACK_PERSPECTIVE = {"h", "g", "f", "e", "d", "c", "b", "a"};


    static void main() {
        System.out.print(ERASE_SCREEN);

        drawChessBoard("WHITE");
        System.out.println();
        drawChessBoard("BLACK");
    }

    public static void drawChessBoard(String teamColor) {
        drawHorizontalBorder(teamColor);

        if (teamColor.equals("WHITE")) {drawChessBoardWhite();}
        else {drawChessBoardBlack();}

        drawHorizontalBorder(teamColor);

        System.out.print(SET_TEXT_COLOR_WHITE);
        System.out.print(RESET_BG_COLOR);
    }

    private static void drawChessBoardWhite() {
        boolean rowStartsWithLight = true;
        for (int i = BOARD_SIZE_IN_SQUARES; i > 0; i--) {
            drawRowOfSquares(rowStartsWithLight, i, "WHITE");
            rowStartsWithLight = !rowStartsWithLight;
        }
    }

    private static void drawChessBoardBlack() {
        boolean rowStartsWithLight = true;
        for (int i = 1; i <= BOARD_SIZE_IN_SQUARES; i++) {
            drawRowOfSquares(rowStartsWithLight, i, "BLACK"); // CHANGE THIS
            rowStartsWithLight = !rowStartsWithLight;
        }
    }

    public static void drawHorizontalBorder(String teamColor) {
        System.out.print(SET_TEXT_COLOR_BLACK);
        printSquare(" ", "Gray");

        if (teamColor.equals("WHITE")) {
            for (String character : BORDER_ROW_WHITE_PERSPECTIVE) {
                printSquare(character, "Gray");
            }
        }
        else {
            for (String character : BORDER_ROW_BLACK_PERSPECTIVE) {
                printSquare(character, "Gray");
            }
        }

        printSquare(" ", "Gray");

        printNewLine();
    }

    public static void drawRowOfSquares(boolean firstSquareIsDark, int rowNumber, String teamColor) {
        printBorderSquare(String.valueOf(rowNumber));

        if (rowNumber == 1 || rowNumber == 2) {System.out.print(SET_TEXT_COLOR_RED);}
        else if (rowNumber == 7 || rowNumber == 8) {System.out.print(SET_TEXT_COLOR_BLUE);}

        for (int i = 0; i < BOARD_SIZE_IN_SQUARES; i++) {

            String character;
            if (rowNumber == 2 || rowNumber == 7) {character = "P";}
            else if (rowNumber == 1 || rowNumber == 8) {
                if (teamColor.equals("WHITE")) {character = ROW_ONE_OR_EIGHT_WHITE_PERSPECTIVE[i];}
                else {character = ROW_ONE_OR_EIGHT_BLACK_PERSPECTIVE[i];}
            }
            else {character = " ";}


            printSquare(character, firstSquareIsDark ? "WHITE" : "BLACK");
            firstSquareIsDark = !firstSquareIsDark;
        }

        printBorderSquare(String.valueOf(rowNumber));

        printNewLine();
    }

    public static void printSquare(String character, String color) {
        if (color.equals("BLACK")) {setBackgroundDark();}
        else if (color.equals("WHITE")) {setBackgroundLight();}
        // it must be the background color
        else {setBackgroundBorderColor();}
        System.out.print(" " + character + " ");
    }

    public static void printBorderSquare(String rowNumber) {
        System.out.print(SET_TEXT_COLOR_BLACK);
        printSquare(rowNumber, "Gray");
    }

    public static void setBackgroundDark() {
        System.out.print(SET_BG_COLOR_BLACK);
    }

    public static void setBackgroundLight() {
        System.out.print(SET_BG_COLOR_WHITE);
    }

    public static void setBackgroundBorderColor() {
        System.out.print(SET_BG_COLOR_LIGHT_GREY);
    }

    private static void setBlack() {
        System.out.print(SET_BG_COLOR_BLACK);
        System.out.print(SET_TEXT_COLOR_BLACK);
    }

    private static void printNewLine() {
        setBlack();
        System.out.println();
    }
}
