package ui;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.*;

public class ChessBoard {

    // Board dimensions
    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private static final int SQUARE_SIZE_IN_PADDED_CHARS = 1;
    private static final int BORDER_WIDTH_IN_PADDED_CHARS = 1;

    // character options when printing a square
    private static final String EMPTY = " ";
    private static final String PAWN = "P";
    private static final String ROOK = "R";
    private static final String KNIGHT = "N";
    private static final String BISHOP = "B";
    private static final String QUEEN = "Q";
    private static final String KING = "K";

    // for each row, excluding border
    private static final String[] ROW_ONE_OR_EIGHT_WHITE_PERSPECTIVE = {ROOK, KNIGHT, BISHOP, QUEEN, KING, BISHOP, KNIGHT, ROOK};
    private static final String[] ROW_ONE_OR_EIGHT_BLACK_PERSPECTIVE = {ROOK, KNIGHT, BISHOP, KING, QUEEN, BISHOP, KNIGHT, ROOK};
    private static final String[] ROW_TWO_OR_SEVEN = {PAWN, PAWN, PAWN, PAWN, PAWN, PAWN, PAWN, PAWN};
    private static final String[] EMPTY_ROW = {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY};

    // for the borders, horizontal and vertical
    private static final String[] BORDER_ROW_WHITE_PERSPECTIVE = {"a", "b", "c", "d", "e", "f", "g", "h"};
    private static final String[] BORDER_ROW_BLACK_PERSPECTIVE = {"h", "g", "f", "e", "d", "c", "b", "a"};

    // these two are bottom to top for the moment
    // wait, I need to somehow access this during the loop for making rows...
    // also I could probably just use a for loop to generate this lol
    private static final String[] ROW_NUMBERS_WHITE_PERSPECTIVE = {"1", "2", "3", "4", "5", "6", "7", "8"};
    private static final String[] ROW_NUMBERS_BLACK_PERSPECTIVE = {"8", "7", "6", "5", "4", "3", "2", "1"};

    // use this to alternate between light and dark squares?
    private boolean nextSquareInRowIsDark;



    public static void main(String[] args) {
        // this seems unnecessary
        // var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        System.out.print(ERASE_SCREEN);

        drawHorizontalHeader("WHITE");
        drawRowOfSquares(true, 8);
        drawRowOfSquares(false, 7);
        drawRowOfSquares(true, 6);
        drawRowOfSquares(false, 5);
        drawRowOfSquares(true, 4);
        drawRowOfSquares(false, 3);
        drawRowOfSquares(true, 2);
        drawRowOfSquares(false, 1);
        drawHorizontalHeader("WHITE");

//        drawChessBoard("WHITE");
//        System.out.println();
//        drawChessBoard("BLACK");



//        drawChessBoard("WHITE");
//        drawChessBoard("BLACK");
//
//        // these seem unnecessary as well
//        System.out.print(SET_BG_COLOR_BLACK);
//        System.out.print(SET_TEXT_COLOR_WHITE);
    }

    public static void drawChessBoard(String teamColor) {
        // get the top border, and then do row by row
        if (teamColor.equals("WHITE")) {drawChessBoardWhite();}
        else {drawChessBoardBlack();}

        drawHorizontalHeader(teamColor);
    }

    private static void drawChessBoardWhite() {
        // use a for loop int i = size; i > 0; i-- ?
        // set isDarkSquare to false ?
        drawHorizontalHeader("WHITE");
        System.out.print(SET_TEXT_COLOR_RED);
        for (int i = BOARD_SIZE_IN_SQUARES; i > 0; i--) {
            drawRowOfSquares(true, 1); // CHANGE THIS
        }

        drawHorizontalHeader("WHITE");
    }

    private static void drawChessBoardBlack() {
        // use a for loop int i = 0; i < size; i++ ?
        // set isDarkSquare to true ?
        drawHorizontalHeader("BLACK");
        for (int i = 0; i < BOARD_SIZE_IN_SQUARES; i++) {
            drawRowOfSquares(true, 1); // CHANGE THIS
        }
        drawHorizontalHeader("BLACK");
    }



    public static void drawHorizontalHeader(String teamColor) {
        // this should make the full line of squares with border colors
        // but, I need to find out how to reverse the order of the letters for the board from black's perspective
        System.out.print(SET_TEXT_COLOR_BLACK);
        printSquare(" ", "Gray");
        for (String character : BORDER_ROW_WHITE_PERSPECTIVE) {
            printSquare(character, "Gray");
        }
        printSquare(" ", "Gray");

        printNewLine();

    }

    public static void drawRowOfSquares(boolean firstSquareIsDark, int rowNumber) {
        // let this take the row string arrays?
        // print a border square with the row number as the text
        // then print out 8 squares, alternating color, with text if a piece is present
        // then print a second border square with the same row number as before
        System.out.print(SET_TEXT_COLOR_BLACK);
        printSquare(String.valueOf(rowNumber), "Gray");
        for (int i = 0; i < BOARD_SIZE_IN_SQUARES; i++) {
            printSquare(" ", firstSquareIsDark ? "WHITE" : "BLACK");
            firstSquareIsDark = !firstSquareIsDark;
        }
        printSquare(String.valueOf(rowNumber), "Gray");

        printNewLine();
    }

    public static void printSquare(String character, String color) {
        if (color.equals("BLACK")) {setBackgroundDark();}
        else if (color.equals("WHITE")) {setBackgroundLight();}
        // it must be the background color
        else {setBackgroundBorderColor();}
        System.out.print(" " + character + " ");
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
