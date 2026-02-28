package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private ChessBoard board = new ChessBoard();
    private TeamColor teamTurn = TeamColor.WHITE;
    private ChessPosition whiteKingLocation = new ChessPosition(1, 5);
    private ChessPosition blackKingLocation = new ChessPosition(8, 5);
    // have some way of tracking moves for en passant?
    // and hold on to some boolean that says if the king or rooks have moved (for castling)?
    // private boolean canCastleKingside = true;
    // private boolean canCastleQueenside = true;


    public ChessGame() {
        // I need to initialize the board somewhere, does this work?
        board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null) {return null;}
        Collection<ChessMove> potentialMoves = piece.pieceMoves(board, startPosition);
        ChessBoard initialBoard = board.clone();
        TeamColor initialTeamTurn = teamTurn;

        Collection<ChessMove> verifiedMoves = new ArrayList<>();
        for (ChessMove move : potentialMoves) {
            try {
                setTeamTurn(piece.getTeamColor());
                makeMove(move);
                verifiedMoves.add(move);
            } catch (InvalidMoveException e) {
                continue;
            } finally {
                this.board = initialBoard.clone();
                setTeamTurn(initialTeamTurn);
                if (piece.getPieceType() == ChessPiece.PieceType.KING) {updateKingPositions();}
            }
        }
        return verifiedMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = board.getPiece(move.getStartPosition());
        if (piece == null) {throw new InvalidMoveException("There is no piece at the specified position");}
        if (piece.getTeamColor() != teamTurn) {throw new InvalidMoveException("You can't move when it is not your turn");}

        Collection<ChessMove> legalMoves = piece.pieceMoves(board, move.getStartPosition());
        if (!legalMoves.contains(move)) {throw new InvalidMoveException("Your piece cannot move to that square");}

        board.addPiece(move.getStartPosition(), null);
        if (move.getPromotionPiece() == null) {board.addPiece(move.getEndPosition(), piece);}
        else {board.addPiece(move.getEndPosition(), new ChessPiece(piece.getTeamColor(), move.getPromotionPiece()));}

        if (piece.getPieceType() == ChessPiece.PieceType.KING) {updateKingPositions();}
        if (isInCheck(teamTurn)) {throw new InvalidMoveException("You cannot make a move that leaves your king in check");}
        switchTeamTurn();
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingSquare = ((teamColor == TeamColor.WHITE) ? whiteKingLocation : blackKingLocation);
        for (ChessPosition position : ChessBoard.allPositions) {
            ChessPiece piece = board.getPiece(position);
            if (piece != null && piece.getTeamColor() != teamColor) {
                if (couldCaptureKing(piece, position, kingSquare)) {return true;}
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {return false;}
        return hasNoValidMoves(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {return false;}
        return hasNoValidMoves(teamColor);
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
        updateKingPositions();
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    private boolean hasNoValidMoves(TeamColor teamColor) {
        for (ChessPosition position : ChessBoard.allPositions) {
            ChessPiece piece = board.getPiece(position);
            if (piece != null && piece.getTeamColor() == teamColor) {
                if (!validMoves(position).isEmpty()) {return false;}
            }
        }
        return true;
    }

    private void updateKingPositions() {
        for (ChessPosition position : ChessBoard.allPositions) {
            ChessPiece piece = board.getPiece(position);
            if (piece != null && piece.getPieceType() == ChessPiece.PieceType.KING) {
                if (piece.getTeamColor() == TeamColor.WHITE) {whiteKingLocation = position;}
                else {blackKingLocation = position;}
            }
        }
    }

    private boolean couldCaptureKing(ChessPiece piece, ChessPosition piecePosition, ChessPosition kingSquare) {
        Collection<ChessMove> moves = piece.pieceMoves(board, piecePosition);
        for (ChessMove move : moves) {
            if (move.getEndPosition().equals(kingSquare)) {
                return true;
            }
        }
        return false;
    }

    private void switchTeamTurn() {
        if (teamTurn == TeamColor.WHITE) {setTeamTurn(TeamColor.BLACK);}
        else {setTeamTurn(TeamColor.WHITE);}
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(board, chessGame.board) && teamTurn == chessGame.teamTurn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, teamTurn);
    }
}