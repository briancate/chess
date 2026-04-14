package ui;

import chess.*;

import java.util.Collection;

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

    public static final boolean [][] EMPTY_BOOLEAN_BOARD = generateEmptyBooleanBoard();

    static boolean [][] generateEmptyBooleanBoard() {
        boolean[][] array = new boolean[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                array[i][j] = false;
            }
        }
        return array;
    }

    static boolean [][] generateFilledBooleanBoardFromPositions(Collection<ChessPosition> positions) {
        if (positions == null) {return EMPTY_BOOLEAN_BOARD;}
        boolean [][] array = new boolean[8][8];
        for (ChessPosition position : chess.ChessBoard.ALL_POSITIONS) {
            if (positions.contains(position)) {
                array[position.getRow()-1][position.getColumn()-1] = true;
            }
        }
        return array;
    }

    static void main() {
        System.out.print(ERASE_SCREEN);

        ChessGame game = new ChessGame();
        try {
            game.makeMove(new ChessMove(new ChessPosition(2, 2), new ChessPosition(4, 2), null));
            game.makeMove(new ChessMove(new ChessPosition(7, 3), new ChessPosition(5, 3), null));
            game.makeMove(new ChessMove(new ChessPosition(1, 7), new ChessPosition(3, 6), null));
            game.makeMove(new ChessMove(new ChessPosition(7, 4), new ChessPosition(6, 4), null));
        }
        catch (InvalidMoveException ex) {
            // this would actually happen in the server I think
            throw new RuntimeException(ex.getMessage()); // only a runtime exception because it's in a main method lol
        }

        // use the position of the piece we want to highlight to find the piece's valid end positions after moving
        Collection<ChessPosition> positions = game.findEndPositionsFromPiecePosition(new ChessPosition(3, 6));

        // use those positions to generate the 2d boolean array
        boolean [][] highlightedSquares = generateFilledBooleanBoardFromPositions(positions);

        drawChessBoard("WHITE", game.getBoard(), highlightedSquares);
        System.out.println();
        drawChessBoard("BLACK", game.getBoard(), EMPTY_BOOLEAN_BOARD);
    }

    public static void drawChessBoard(String teamColor, chess.ChessBoard board, boolean [][] squaresToHighlight) {
        drawHorizontalBorder(teamColor);

        if (teamColor.equals("WHITE")) {drawChessBoardWhite(board, squaresToHighlight);}
        else {drawChessBoardBlack(board, squaresToHighlight);}

        drawHorizontalBorder(teamColor);

        System.out.print(SET_TEXT_COLOR_WHITE);
        System.out.print(RESET_BG_COLOR);
    }

    public static void drawHorizontalBorder(String teamColor) {
        printSquare(" ", "GRAY", "BLACK", false);
        if (teamColor.equals("WHITE")) {
            for (String character : BORDER_ROW_WHITE_PERSPECTIVE) {
                printSquare(character, "GRAY", "BLACK", false);
            }
        }
        else {
            for (String character : BORDER_ROW_BLACK_PERSPECTIVE) {
                printSquare(character, "GRAY", "BLACK", false);
            }
        }
        printSquare(" ", "GRAY","BLACK", false);

        printNewLine();
    }

    private static void drawChessBoardWhite(chess.ChessBoard board, boolean [][] squaresToHighlight) {
        boolean rowStartsWithLight = true;
        for (int i = BOARD_SIZE_IN_SQUARES; i > 0; i--) {
            drawRowOfSquares(rowStartsWithLight, i, "WHITE", board, squaresToHighlight);
            rowStartsWithLight = !rowStartsWithLight;
        }
    }

    private static void drawChessBoardBlack(chess.ChessBoard board, boolean [][] squaresToHighlight) {
        boolean rowStartsWithLight = true;
        for (int i = 1; i <= BOARD_SIZE_IN_SQUARES; i++) {
            drawRowOfSquares(rowStartsWithLight, i, "BLACK", board, squaresToHighlight);
            rowStartsWithLight = !rowStartsWithLight;
        }
    }

    public static void drawRowOfSquares(boolean firstSquareIsDark, int rowNumber, String teamColor, chess.ChessBoard board,
                                        boolean [][] squaresToHighlight) {
        printSquare(String.valueOf(rowNumber), "GRAY", "BLACK", false);

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

            boolean isHighlighted = squaresToHighlight[rowNumber-1][colNumber-1];
//            if (isHighlighted) {System.out.println("Row number: " + rowNumber + " Col number: " + colNumber);}

            String squareColor = firstSquareIsDark ? "WHITE" : "BLACK";
            printSquare(character, squareColor, pieceColor, isHighlighted);
            firstSquareIsDark = !firstSquareIsDark;
        }
        printSquare(String.valueOf(rowNumber), "GRAY", "BLACK", false);

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

    public static void printSquare(String character, String squareColor, String textColor, boolean isHighlighted) {
        // set the text color
        switch (textColor) {
            case "BLACK" -> System.out.print(SET_TEXT_COLOR_BLACK);
            case "BLUE" -> System.out.print(SET_TEXT_COLOR_BLUE);
            case "RED" -> System.out.print(SET_TEXT_COLOR_RED);
        }

        // set the background color
        if (squareColor.equals("BLACK")) {
            if (isHighlighted) {setBackgroundHighlightedDark();}
            else {setBackgroundDark();}
        }
        else if (squareColor.equals("WHITE")) {
            if (isHighlighted) {setBackgroundHighlightedLight();}
            else {setBackgroundLight();}
        }
        else {
            setBackgroundBorderColor();
        }
        System.out.print(" " + character + " ");
    }

    public static void setBackgroundHighlightedDark() {
        System.out.print(SET_BG_COLOR_DARK_GREEN);
    }

    public static void setBackgroundHighlightedLight() {
        System.out.print(SET_BG_COLOR_GREEN);
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
