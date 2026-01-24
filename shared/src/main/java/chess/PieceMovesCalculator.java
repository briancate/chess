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
        if (position.getRow() > 8 || position.getColumn() > 8) {return true;}
        return position.getRow() < 1 || position.getColumn() < 1;
    }

    private boolean canMove(ChessPosition position) {
        // this checks for allied pieces or out of bounds, doesn't check for a piece blocking movement to the square
        // doesn't work for pawns
        if (isOutOfBounds(position)) {return false;}
        return !(isOccupied(position)) || !(isAlly(position));
    }

    private ChessMove addPosition(int changeRow, int changeCol) {
        ChessPosition newPosition = new ChessPosition(myPosition.getRow() + changeRow, myPosition.getColumn() + changeCol);
        if (canMove(newPosition)) {return new ChessMove(myPosition, newPosition, null);}
        return null;
    }

    private void addMovesInALine(Collection<ChessMove> array, int changeRow, int changeCol) {
        for (int i = 1; i < 8; i++) {
            ChessPosition positionToAdd = new ChessPosition(myPosition.getRow() + (i * changeRow), myPosition.getColumn() + (i * changeCol));
            // if it's out of bounds or an ally, don't add the move to the array
            if (isOutOfBounds(positionToAdd)) {break;}
            if (isOccupied(positionToAdd) && isAlly(positionToAdd)) {break;} // could canMove catch both these lines?
            // if it isn't, add it
            array.add(new ChessMove(myPosition, positionToAdd, null));
            // if the piece was an enemy (a valid move), don't continue the line
            if (isOccupied(positionToAdd) && !(isAlly(positionToAdd))) {break;}
        }
    }

    private void generatePawnPromotionMoves(Collection<ChessMove> array, ChessPosition position) {
        array.add(new ChessMove(myPosition, position, ChessPiece.PieceType.KNIGHT));
        array.add(new ChessMove(myPosition, position, ChessPiece.PieceType.BISHOP));
        array.add(new ChessMove(myPosition, position, ChessPiece.PieceType.ROOK));
        array.add(new ChessMove(myPosition, position, ChessPiece.PieceType.QUEEN));
    }

    public Collection<ChessMove> generateMoves() {
        ArrayList<ChessMove> moves = new ArrayList<>();
        if (piece.getPieceType() == ChessPiece.PieceType.QUEEN) {QueenMovesCalculator(moves);}
        if (piece.getPieceType() == ChessPiece.PieceType.ROOK) {RookMovesCalculator(moves);}
        if (piece.getPieceType() == ChessPiece.PieceType.BISHOP) {BishopMovesCalculator(moves);}
        if (piece.getPieceType() == ChessPiece.PieceType.KING) {KingMovesCalculator(moves);}
        if (piece.getPieceType() == ChessPiece.PieceType.KNIGHT) {KnightMovesCalculator(moves);}
        if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {WhitePawnMovesGenerator(moves);}
            if (piece.getTeamColor() == ChessGame.TeamColor.BLACK) {BlackPawnMovesGenerator(moves);}
        }
        // Should I return an empty arraylist instead?
        return moves;
    }

    private void KingMovesCalculator(Collection<ChessMove> array) {
        // could I instead do something like List.of([[-1, 1], [-1, 0], [-1, -1], etc.]) ?
        // then do a for (potential move : List) to make this cleaner?
        // List<List<Integer>> = [[-1, -1], [-1, 0], [-1, 1], [0, -1], [0 , 1], [1, -1], [1, 0], [1, 1]]
        if (addPosition(-1, -1) != null) {array.add(addPosition(-1, -1));}
        if (addPosition(-1, 0) != null) {array.add(addPosition(-1, 0));}
        if (addPosition(-1, 1) != null) {array.add(addPosition(-1, 1));}
        if (addPosition(0, -1) != null) {array.add(addPosition(0, -1));}
        if (addPosition(0, 1) != null) {array.add(addPosition(0, 1));}
        if (addPosition(1, -1) != null) {array.add(addPosition(1, -1));}
        if (addPosition(1, 0) != null) {array.add(addPosition(1, 0));}
        if (addPosition(1, 1) != null) {array.add(addPosition(1, 1));}
//        return array;
    }

    private void KnightMovesCalculator(Collection<ChessMove> array) {
        if (addPosition(-2, -1) != null) {array.add(addPosition(-2, -1));}
        if (addPosition(-2, 1) != null) {array.add(addPosition(-2, 1));}
        if (addPosition(-1, -2) != null) {array.add(addPosition(-1, -2));}
        if (addPosition(-1, 2) != null) {array.add(addPosition(-1, 2));}
        if (addPosition(1, -2) != null) {array.add(addPosition(1, -2));}
        if (addPosition(1, 2) != null) {array.add(addPosition(1, 2));}
        if (addPosition(2, -1) != null) {array.add(addPosition(2, -1));}
        if (addPosition(2, 1) != null) {array.add(addPosition(2, 1));}
//        return array;
    }

    private void BishopMovesCalculator(Collection<ChessMove> array) {
        addMovesInALine(array, -1, -1);
        addMovesInALine(array, -1, 1);
        addMovesInALine(array, 1, -1);
        addMovesInALine(array, 1, 1);
//        return array;
    }

    private void RookMovesCalculator(Collection<ChessMove> array) {
        addMovesInALine(array, -1, 0);
        addMovesInALine(array, 0, -1);
        addMovesInALine(array, 0, 1);
        addMovesInALine(array, 1, 0);
//        return array;
    }

    private void QueenMovesCalculator(Collection<ChessMove> array) {
        addMovesInALine(array, -1, 0);
        addMovesInALine(array, 0, -1);
        addMovesInALine(array, 0, 1);
        addMovesInALine(array, 1, 0);
        addMovesInALine(array, -1, -1);
        addMovesInALine(array, -1, 1);
        addMovesInALine(array, 1, -1);
        addMovesInALine(array, 1, 1);
//        return array;
    }

    private void WhitePawnMovesGenerator(Collection<ChessMove> array) {

        ChessPosition front = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn());
        ChessPosition diagonalLeft = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 1);
        ChessPosition diagonalRight = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 1);

        if (myPosition.getRow() == 2) {
            ChessPosition frontTwice = new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn());
            if (!isOccupied(front) && ! isOccupied(frontTwice)) {
                array.add(new ChessMove(myPosition, frontTwice, null));
            }
        }
        if (myPosition.getRow() != 7) {
            if (!isOccupied(front)) {array.add(new ChessMove(myPosition, front, null));}
            if (!(isOutOfBounds(diagonalLeft)) && isOccupied(diagonalLeft) && !(isAlly(diagonalLeft))) {array.add(new ChessMove(myPosition, diagonalLeft, null));}
            if (!(isOutOfBounds(diagonalRight)) && isOccupied(diagonalRight) && !(isAlly(diagonalRight))) {array.add(new ChessMove(myPosition, diagonalRight, null));}
        }
        else {
            if (!isOccupied(front)) {generatePawnPromotionMoves(array, front);}
            if (!(isOutOfBounds(diagonalLeft)) && isOccupied(diagonalLeft) && !(isAlly(diagonalLeft))) {generatePawnPromotionMoves(array, diagonalLeft);}
            if (!(isOutOfBounds(diagonalRight)) && isOccupied(diagonalRight) && !(isAlly(diagonalRight))) {generatePawnPromotionMoves(array, diagonalRight);}
        }
//        return array;
    }

        private void BlackPawnMovesGenerator(Collection<ChessMove> array) {
            // code to set up the 3 potential pawn moves
            ChessPosition front = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn());
            ChessPosition diagonalLeft = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 1);
            ChessPosition diagonalRight = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 1);

            if (myPosition.getRow() == 7) {
                ChessPosition frontTwice = new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn());
                if (!isOccupied(front) && ! isOccupied(frontTwice)) {
                    array.add(new ChessMove(myPosition, frontTwice, null));
                }
            }
            if (myPosition.getRow() != 2) {
                if (!isOccupied(front)) {array.add(new ChessMove(myPosition, front, null));}
                if (!(isOutOfBounds(diagonalLeft)) && isOccupied(diagonalLeft) && !(isAlly(diagonalLeft))) {array.add(new ChessMove(myPosition, diagonalLeft, null));}
                if (!(isOutOfBounds(diagonalRight)) && isOccupied(diagonalRight) && !(isAlly(diagonalRight))) {array.add(new ChessMove(myPosition, diagonalRight, null));}
            }
            else {
                if (!isOccupied(front)) {generatePawnPromotionMoves(array, front);}
                if (!(isOutOfBounds(diagonalLeft)) && isOccupied(diagonalLeft) && !(isAlly(diagonalLeft))) {generatePawnPromotionMoves(array, diagonalLeft);}
                if (!(isOutOfBounds(diagonalRight)) && isOccupied(diagonalRight) && !(isAlly(diagonalRight))) {generatePawnPromotionMoves(array, diagonalRight);}
            }
//            return array;
    }
}

