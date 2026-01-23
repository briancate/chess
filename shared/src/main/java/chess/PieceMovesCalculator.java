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
        if (isOutOfBounds(position)) {return false;}
        return !(isOccupied(position)) || !(isAlly(position));
    }

    private ChessMove addPosition(int changeRow, int changeCol) {
        ChessPosition newPosition = new ChessPosition(myPosition.getRow() + changeRow, myPosition.getColumn() + changeCol);
        if (canMove(newPosition)) {
            // implement this if a pawn reaches the back rank
            // if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {return new ChessMove(myPosition, position, )}
            return new ChessMove(myPosition, newPosition, null);
        }
        return null;
    }

    private void addMovesInALine(Collection<ChessMove> array, int changeRow, int changeCol) {
//        ArrayList<ChessMove> moves = new ArrayList<>();
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
//        return array;
    }

    public Collection<ChessMove> generateMoves() {
        // I was told to use ArrayLists for this, apparently they let you add things to the list
        /* if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {return PawnMovesCalculator(piece, board, myPosition);}
        if (piece.getPieceType() == ChessPiece.PieceType.BISHOP) {
            return List.of(new ChessMove(new ChessPosition(5,4), new ChessPosition(1,8), null));
        }
        if (piece.getPieceType() == ChessPiece.PieceType.ROOK) {return List.of();}

        if (piece.getPieceType() == ChessPiece.PieceType.QUEEN) {return List.of();} */

        if (piece.getPieceType() == ChessPiece.PieceType.ROOK) {return RookMovesCalculator();}
        if (piece.getPieceType() == ChessPiece.PieceType.BISHOP) {return BishopMovesCalculator();}
        if (piece.getPieceType() == ChessPiece.PieceType.KING) {return KingMovesCalculator();}
        if (piece.getPieceType() == ChessPiece.PieceType.KNIGHT) {return KnightMovesCalculator();}
        // delete this later
        return null;
    }

    private Collection<ChessMove> KingMovesCalculator() {
        ArrayList<ChessMove> moves = new ArrayList<>();
        // could I instead do something like List.of([[-1, 1], [-1, 0], [-1, -1], etc.]) ?
        // then do a for (potential move : List) to make this cleaner?
        // List<List<Integer>> = [[-1, -1], [-1, 0], [-1, 1], [0, -1], [0 , 1], [1, -1], [1, 0], [1, 1]]
        if (addPosition(-1, -1) != null) {moves.add(addPosition(-1, -1));}
        if (addPosition(-1, 0) != null) {moves.add(addPosition(-1, 0));}
        if (addPosition(-1, 1) != null) {moves.add(addPosition(-1, 1));}
        if (addPosition(0, -1) != null) {moves.add(addPosition(0, -1));}
        if (addPosition(0, 1) != null) {moves.add(addPosition(0, 1));}
        if (addPosition(1, -1) != null) {moves.add(addPosition(1, -1));}
        if (addPosition(1, 0) != null) {moves.add(addPosition(1, 0));}
        if (addPosition(1, 1) != null) {moves.add(addPosition(1, 1));}
        return moves;
    }

    private Collection<ChessMove> KnightMovesCalculator() {
        ArrayList<ChessMove> moves = new ArrayList<>();
        if (addPosition(-2, -1) != null) {moves.add(addPosition(-2, -1));}
        if (addPosition(-2, 1) != null) {moves.add(addPosition(-2, 1));}
        if (addPosition(-1, -2) != null) {moves.add(addPosition(-1, -2));}
        if (addPosition(-1, 2) != null) {moves.add(addPosition(-1, 2));}
        if (addPosition(1, -2) != null) {moves.add(addPosition(1, -2));}
        if (addPosition(1, 2) != null) {moves.add(addPosition(1, 2));}
        if (addPosition(2, -1) != null) {moves.add(addPosition(2, -1));}
        if (addPosition(2, 1) != null) {moves.add(addPosition(2, 1));}
        return moves;
    }

    private Collection<ChessMove> BishopMovesCalculator() {
        ArrayList<ChessMove> moves = new ArrayList<>();
        addMovesInALine(moves, -1, -1);
        addMovesInALine(moves, -1, 1);
        addMovesInALine(moves, 1, -1);
        addMovesInALine(moves, 1, 1);
        return moves;
    }

    private Collection<ChessMove> RookMovesCalculator() {
        ArrayList<ChessMove> moves = new ArrayList<>();
        addMovesInALine(moves, -1, 0);
        addMovesInALine(moves, 0, -1);
        addMovesInALine(moves, 0, 1);
        addMovesInALine(moves, 1, 0);
        return moves;
    }

//    private Collection<ChessMove> PawnMovesCalculator() {
//        ArrayList<ChessMove> moves = new ArrayList<>();
//        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
//            moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn()), null));
//        }
//        else {moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow()-1, myPosition.getColumn()), null));}
//        return moves;
//    }
}

