package ui;

import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

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
    private static final String PAWN = "P";

    // for the borders, horizontal and vertical
    private static final String[] BORDER_ROW_WHITE_PERSPECTIVE = {"a", "b", "c", "d", "e", "f", "g", "h"};
    private static final String[] BORDER_ROW_BLACK_PERSPECTIVE = {"h", "g", "f", "e", "d", "c", "b", "a"};


    static void main() {
        System.out.print(ERASE_SCREEN);

        ChessGame game = new ChessGame();
        game.getBoard().resetBoard();

        drawChessBoard("WHITE", game.getBoard());
        System.out.println();
        drawChessBoard("BLACK", game.getBoard());
    }

    public static void drawChessBoard(String teamColor, chess.ChessBoard board) {
        drawHorizontalBorder(teamColor);

        if (teamColor.equals("WHITE")) {drawChessBoardWhite(board);}
        else {drawChessBoardBlack(board);}

        drawHorizontalBorder(teamColor);

        System.out.print(SET_TEXT_COLOR_WHITE);
        System.out.print(RESET_BG_COLOR);
    }

    public static void drawHorizontalBorder(String teamColor) {
        printSquare(" ", "GRAY", "BLACK");
        if (teamColor.equals("WHITE")) {
            for (String character : BORDER_ROW_WHITE_PERSPECTIVE) {
                printSquare(character, "GRAY", "BLACK");
            }
        }
        else {
            for (String character : BORDER_ROW_BLACK_PERSPECTIVE) {
                printSquare(character, "GRAY", "BLACK");
            }
        }
        printSquare(" ", "GRAY","BLACK");

        printNewLine();
    }

    private static void drawChessBoardWhite(chess.ChessBoard board) {
        boolean rowStartsWithLight = true;
        for (int i = BOARD_SIZE_IN_SQUARES; i > 0; i--) {
            drawRowOfSquares(rowStartsWithLight, i, "WHITE", board);
            rowStartsWithLight = !rowStartsWithLight;
        }
    }

    private static void drawChessBoardBlack(chess.ChessBoard board) {
        boolean rowStartsWithLight = true;
        for (int i = 1; i <= BOARD_SIZE_IN_SQUARES; i++) {
            drawRowOfSquares(rowStartsWithLight, i, "BLACK", board);
            rowStartsWithLight = !rowStartsWithLight;
        }
    }

    public static void drawRowOfSquares(boolean firstSquareIsDark, int rowNumber, String teamColor, chess.ChessBoard board) {
        printSquare(String.valueOf(rowNumber), "GRAY", "BLACK");

        for (int i = 1; i <= BOARD_SIZE_IN_SQUARES; i++) {
            String character;
            String pieceColor;

            int colNumber;
            if (teamColor.equals("WHITE")) {colNumber = i;}
            else {colNumber = BOARD_SIZE_IN_SQUARES + 1 - i;}

            chess.ChessPiece piece = board.getPiece(new ChessPosition(rowNumber, colNumber));
            if (piece == null) {
                character = " ";
                pieceColor = "BLACK"; // the text color shouldn't matter because we're printing a space
            }
            else {
                character = getCharacterFromPieceType(piece);
                pieceColor = getPieceColorFromPiece(piece);
            }

            String squareColor = firstSquareIsDark ? "WHITE" : "BLACK";
            printSquare(character, squareColor, pieceColor);
            firstSquareIsDark = !firstSquareIsDark;
        }
        printSquare(String.valueOf(rowNumber), "GRAY", "BLACK");

        printNewLine();
    }


    private static String getCharacterFromPieceType(ChessPiece piece) {
        String character;
        ChessPiece.PieceType type = piece.getPieceType();
        switch (type) {
            case KING -> character = KING;
            case QUEEN -> character = QUEEN;
            case BISHOP -> character = BISHOP;
            case KNIGHT -> character = KNIGHT;
            case ROOK -> character = ROOK;
            case PAWN -> character = PAWN;
            default -> character = " "; // shouldn't be necessary, but the IDE wants it
        }
        return character;
    }

    private static String getPieceColorFromPiece(ChessPiece piece) {
        String pieceColor;
        ChessGame.TeamColor pieceTeamColor = piece.getTeamColor();
        switch (pieceTeamColor) {
            case WHITE -> pieceColor = "RED";
            case BLACK -> pieceColor = "BLUE";
            default -> pieceColor = "BLACK"; // shouldn't ever be used but my IDE complains if I don't have a default
        }
        return pieceColor;
    }

    public static void printSquare(String character, String squareColor, String textColor) {
        // set the background color
        switch (squareColor) {
            case "BLACK" -> setBackgroundDark();
            case "WHITE" -> setBackgroundLight();
            case "GRAY" -> setBackgroundBorderColor();
        }
        // set the text color
        switch (textColor) {
            case "BLACK" -> System.out.print(SET_TEXT_COLOR_BLACK);
            case "BLUE" -> System.out.print(SET_TEXT_COLOR_BLUE);
            case "RED" -> System.out.print(SET_TEXT_COLOR_RED);
        }
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

    private static void printNewLine() {
        System.out.print(RESET_BG_COLOR);
        System.out.println();
    }
}
