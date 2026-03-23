package ui;

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



    static void main() {
        // this seems unnecessary
        // var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        System.out.print(ERASE_SCREEN);

//        drawHorizontalHeader("WHITE");
//        drawRowOfSquares(true, 8);
//        drawRowOfSquares(false, 7);
//        drawRowOfSquares(true, 6);
//        drawRowOfSquares(false, 5);
//        drawRowOfSquares(true, 4);
//        drawRowOfSquares(false, 3);
//        drawRowOfSquares(true, 2);
//        drawRowOfSquares(false, 1);
//        drawHorizontalHeader("WHITE");

        drawChessBoard("WHITE");
        System.out.println();
        drawChessBoard("BLACK");



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
    }

    private static void drawChessBoardWhite() {
        // use a for loop int i = size; i > 0; i-- ?
        // set isDarkSquare to false ?
        drawHorizontalBorder("WHITE");

//        System.out.print(SET_TEXT_COLOR_RED);
        boolean rowStartsWithLight = true;
        for (int i = BOARD_SIZE_IN_SQUARES; i > 0; i--) {
            drawRowOfSquares(rowStartsWithLight, i, "WHITE");
            rowStartsWithLight = !rowStartsWithLight;
        }

        drawHorizontalBorder("WHITE");
    }

    private static void drawChessBoardBlack() {
        // use a for loop int i = 0; i < size; i++ ?
        // set isDarkSquare to true ?
        drawHorizontalBorder("BLACK");

        boolean rowStartsWithLight = true;
        for (int i = 1; i <= BOARD_SIZE_IN_SQUARES; i++) {
            drawRowOfSquares(rowStartsWithLight, i, "BLACK"); // CHANGE THIS
            rowStartsWithLight = !rowStartsWithLight;
        }

        drawHorizontalBorder("BLACK");
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
