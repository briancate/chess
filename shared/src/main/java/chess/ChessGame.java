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
        // this doesn't have to worry about who's turn it is
        if (board.getPiece(startPosition) == null) {return null;}
        Collection<ChessMove> potentialMoves = board.getPiece(startPosition).pieceMoves(board, startPosition);
        // from here, loop through the moves, remove those that aren't valid (leave the King in check)
        ChessBoard initialBoard = board.clone(); // use this to save the current board
        TeamColor initialTeamTurn = teamTurn;

        Collection<ChessMove> verifiedMoves = new ArrayList<>();
        for (ChessMove move : potentialMoves) {
            try {
                makeMove(move);
                verifiedMoves.add(move);
            } catch (InvalidMoveException e) {
                // potentialMoves.remove(move);
//                System.out.println(e);
            } finally {
                // restore the board after each move, valid or not
                this.board = initialBoard.clone();
                setTeamTurn(initialTeamTurn);
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
        // throw an error if it's not your turn or if there's no piece at the starting position
        if (piece == null) {throw new InvalidMoveException("There is no piece at the specified position");}
        if (piece.getTeamColor() != teamTurn) {throw new InvalidMoveException("It is not your turn lol");}
        // rule out invalid moves first
        Collection<ChessMove> legalMoves = piece.pieceMoves(board, move.getStartPosition());
        // Collection<ChessMove> legalMoves = validMoves(move.getStartPosition());
        if (!legalMoves.contains(move)) {throw new InvalidMoveException("Your piece cannot move to that square");}

        // need to make the start position null (since the piece moved) and the new position the new piece
        board.addPiece(move.getStartPosition(), null);
        // that should automatically get rid of the enemy piece (if applicable), right?
        if (move.getPromotionPiece() == null) {board.addPiece(move.getEndPosition(), piece);}
        else {board.addPiece(move.getEndPosition(), new ChessPiece(piece.getTeamColor(), move.getPromotionPiece()));}

        // update kingLocation if you move the king, make this a function?
        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            if (piece.getTeamColor() == TeamColor.WHITE) {whiteKingLocation = move.getEndPosition();}
            else {blackKingLocation = move.getEndPosition();}
        }

        // throw an exception here if the move results in check?
        if (isInCheck(teamTurn)) {throw new InvalidMoveException("You cannot make a move that leaves your king in check");}

        // update whose turn it is
        // teamTurn = ((teamTurn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE);
        if (teamTurn == TeamColor.WHITE) {setTeamTurn(TeamColor.BLACK);}
        else {setTeamTurn(TeamColor.WHITE);}
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        // I need to loop over every square on the board, find the moves of each enemy piece,
        // then return if a piece could capture the King, check if the given teamColor is in check
        ChessPosition kingSquare = ((teamColor == TeamColor.WHITE) ? whiteKingLocation : blackKingLocation);

        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition square = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(square);
                if (piece != null) {
                    if (piece.getTeamColor() != teamColor) {
                        Collection<ChessMove> moves = piece.pieceMoves(board, square);
                        for (ChessMove move : moves) {
                            // if (move.getEndPosition().getRow() == kingSquare.getRow() && move.getEndPosition().getColumn() == kingSquare.getColumn()) {
                            // if (move.getEndPosition() == kingSquare) {
                            if (move.getEndPosition().equals(kingSquare)) {
                                return true;
                            }
                        }
                    }
                }
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
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
        // dang I would need to update king positions...
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition square = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(square);
                if (piece != null) {
                    if (piece.getPieceType() == ChessPiece.PieceType.KING) {
                        if (piece.getTeamColor() == TeamColor.WHITE) {whiteKingLocation = square;}
                        else {blackKingLocation = square;}
                    }
                }
            }
        }
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
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

// WRITE A FUNCTION THAT CAN ITERATE OVER THE BOARD
// Or maybe just make a function that returns a list of each ChessPosition, that you can then use elsewhere

// How on Earth can I use validMoves to get moves regardless of turn if I need makeMove to not allow moves if it's not your turn?
// I guess that means I can't actually use the makeMove function to see if it's valid
// So I guess I make other methods that check everything except if it's your turn?
