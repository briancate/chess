package websocket.commands;

import chess.ChessMove;

public class MakeMoveCommand extends UserGameCommand {

    private final ChessMove move;
    private final String teamColor;

    public MakeMoveCommand(String authToken, int gameID, ChessMove move, String teamColor) {
        super(CommandType.MAKE_MOVE, authToken, gameID);
        this.move = move;
        this.teamColor = teamColor;
    }

    public ChessMove getMove() {
        return this.move;
    }

    public String getTeamColor() {
        return teamColor;
    }
}
