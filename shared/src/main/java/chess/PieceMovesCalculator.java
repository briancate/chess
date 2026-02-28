package chess;
import java.util.ArrayList;
import java.util.Collection;

public class PieceMovesCalculator {

    private final ChessBoard board;
    private final ChessPosition myPosition;
    private final ChessPiece piece;

    public PieceMovesCalculator(ChessBoard board, ChessPosition myPosition) {
        this.board = board;
        this.myPosition = myPosition;
        this.piece = board.getPiece(myPosition);
    }

    private boolean isAlly(ChessPosition position) {
        // returns true if an allied piece is at the position, assumes the position is occupied (doesn't check for null)
        return board.getPiece(position).getTeamColor() == piece.getTeamColor();
    }   

    private boolean isOccupied(ChessPosition position) {
        // returns true if a piece is at the position, false otherwise
        return board.getPiece(position) != null;
    }

    private boolean isOutOfBounds(ChessPosition position) {
        return position.getRow() < 1 || position.getColumn() < 1 || position.getRow() > 8 || position.getColumn() > 8;
    }

    private boolean canMove(ChessPosition position) {
        // checks for allied pieces or out of bounds, doesn't check for a piece blocking movement to the square
        // doesn't work for pawns
        if (isOutOfBounds(position)) {return false;}
        return !(isOccupied(position)) || !(isAlly(position));
    }

    private void addMovesInALine(Collection<ChessMove> array, int changeRow, int changeCol) {
        for (int i = 1; i < 8; i++) {
            ChessPosition positionToAdd = new ChessPosition(myPosition.getRow() + (i * changeRow), myPosition.getColumn() + (i * changeCol));
            // if the position is out of bounds or contains an ally, don't add the move to the array
            if (!canMove(positionToAdd)) {break;}
            // if it isn't, add it
            array.add(new ChessMove(myPosition, positionToAdd, null));
            // if the piece was an enemy (a valid move), don't continue the line
            if (isOccupied(positionToAdd) && !(isAlly(positionToAdd))) {break;}
        }
    }

    private void generateMovesFromList(Collection<ChessMove> array, int[][] list) {
        // accepts a list of pairs indicating changes to row or column, adds all that are valid to the moves array
        for (int[] pair : list) {
            ChessPosition newPosition = new ChessPosition(myPosition.getRow() + pair[0], myPosition.getColumn() + pair[1]);
            if (canMove(newPosition)) {array.add(new ChessMove(myPosition, newPosition, null));}
        }
    }

    private boolean validatePawnDiagonalMoves(ChessPosition position) {
        return !(isOutOfBounds(position)) && isOccupied(position) && !(isAlly(position));
    }

    private void generatePawnPromotionMoves(Collection<ChessMove> array, ChessPosition position) {
        array.add(new ChessMove(myPosition, position, ChessPiece.PieceType.KNIGHT));
        array.add(new ChessMove(myPosition, position, ChessPiece.PieceType.BISHOP));
        array.add(new ChessMove(myPosition, position, ChessPiece.PieceType.ROOK));
        array.add(new ChessMove(myPosition, position, ChessPiece.PieceType.QUEEN));
    }

    public Collection<ChessMove> generateMoves() {
        ArrayList<ChessMove> moves = new ArrayList<>();
        if (piece.getPieceType() == ChessPiece.PieceType.QUEEN) {queenMovesCalculator(moves);}
        if (piece.getPieceType() == ChessPiece.PieceType.ROOK) {rookMovesCalculator(moves);}
        if (piece.getPieceType() == ChessPiece.PieceType.BISHOP) {bishopMovesCalculator(moves);}
        if (piece.getPieceType() == ChessPiece.PieceType.KING) {kingMovesCalculator(moves);}
        if (piece.getPieceType() == ChessPiece.PieceType.KNIGHT) {knightMovesCalculator(moves);}
        if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {whitePawnMovesGenerator(moves);}
            if (piece.getTeamColor() == ChessGame.TeamColor.BLACK) {blackPawnMovesGenerator(moves);}
        }
        return moves;
    }

    private void kingMovesCalculator(Collection<ChessMove> array) {
        int[][] possibleMoves = {{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0 , 1}, {1, -1}, {1, 0}, {1, 1}};
        generateMovesFromList(array, possibleMoves);
    }

    private void knightMovesCalculator(Collection<ChessMove> array) {
        int[][] possibleMoves = {{-2, -1},{-2, 1},{-1, -2},{-1, 2},{1, -2},{1, 2},{2, -1},{2, 1}};
        generateMovesFromList(array, possibleMoves);
    }

    private void bishopMovesCalculator(Collection<ChessMove> array) {
        addMovesInALine(array, -1, -1);
        addMovesInALine(array, -1, 1);
        addMovesInALine(array, 1, -1);
        addMovesInALine(array, 1, 1);
    }

    private void rookMovesCalculator(Collection<ChessMove> array) {
        addMovesInALine(array, -1, 0);
        addMovesInALine(array, 0, -1);
        addMovesInALine(array, 0, 1);
        addMovesInALine(array, 1, 0);
    }

    private void queenMovesCalculator(Collection<ChessMove> array) {
        // add the moves a rook could make, then the moves a bishop could make
        rookMovesCalculator(array);
        bishopMovesCalculator(array);
    }

    private void pawnMovesGenerator(Collection<ChessMove> array, ChessPosition front, ChessPosition diagonalLeft,
                                    ChessPosition diagonalRight, int promotionRow) {
        if (myPosition.getRow() != promotionRow) {
            if (!isOccupied(front)) {array.add(new ChessMove(myPosition, front, null));}
            if (validatePawnDiagonalMoves(diagonalLeft)) {
                array.add(new ChessMove(myPosition, diagonalLeft, null));}
            if (validatePawnDiagonalMoves(diagonalRight)) {
                array.add(new ChessMove(myPosition, diagonalRight, null));}
        }
        else {
            if (!isOccupied(front)) {generatePawnPromotionMoves(array, front);}
            if (validatePawnDiagonalMoves(diagonalLeft)) {generatePawnPromotionMoves(array, diagonalLeft);}
            if (validatePawnDiagonalMoves(diagonalRight)) {generatePawnPromotionMoves(array, diagonalRight);}
        }
    }

    private void whitePawnMovesGenerator(Collection<ChessMove> array) {
        ChessPosition front = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn());
        ChessPosition diagonalLeft = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 1);
        ChessPosition diagonalRight = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 1);
        if (myPosition.getRow() == 2) {
            ChessPosition frontTwice = new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn());
            if (!isOccupied(front) && ! isOccupied(frontTwice)) {
                array.add(new ChessMove(myPosition, frontTwice, null));
            }
        }
        pawnMovesGenerator(array, front, diagonalLeft, diagonalRight, 7);
    }

    private void blackPawnMovesGenerator(Collection<ChessMove> array) {
        ChessPosition front = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn());
        ChessPosition diagonalLeft = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 1);
        ChessPosition diagonalRight = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 1);
        if (myPosition.getRow() == 7) {
            ChessPosition frontTwice = new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn());
            if (!isOccupied(front) && ! isOccupied(frontTwice)) {
                array.add(new ChessMove(myPosition, frontTwice, null));
            }
        }
        pawnMovesGenerator(array, front, diagonalLeft, diagonalRight, 2);
    }
}