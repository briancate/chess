package chess;
import java.util.ArrayList;
import java.util.Collection;
// delete this later
import java.util.List;


public class PieceMovesCalculator {

    private final ChessBoard board;
    private final ChessPosition myPosition;

    public PieceMovesCalculator(ChessBoard board, ChessPosition myPosition) {
        this.board = board;
        this.myPosition = myPosition;
    }

    public Collection<ChessMove> GenerateMoves(ChessBoard board, ChessPosition myPosition) {
        // I was told to use ArrayLists for this, apparently they let you add things to the list
        ChessPiece piece = board.getPiece(myPosition);
        if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {return PawnMovesCalculator(piece, board, myPosition);} /*
        if (piece.getPieceType() == ChessPiece.PieceType.BISHOP) {
            return List.of(new ChessMove(new ChessPosition(5,4), new ChessPosition(1,8), null));
        }
        if (piece.getPieceType() == ChessPiece.PieceType.ROOK) {return List.of();}
        if (piece.getPieceType() == ChessPiece.PieceType.KNIGHT) {return List.of();}
        if (piece.getPieceType() == ChessPiece.PieceType.QUEEN) {return List.of();}
        if (piece.getPieceType() == ChessPiece.PieceType.KING) {return List.of();} */
        // delete this later
        ArrayList<ChessMove> moves = new ArrayList<>();
        return moves;
    }

    private Collection<ChessMove> PawnMovesCalculator(ChessPiece piece, ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn()), null));
        }
        else {moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow()-1, myPosition.getColumn()), null));}
        return moves;
    }
}
