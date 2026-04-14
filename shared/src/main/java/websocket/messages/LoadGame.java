package websocket.messages;

import chess.ChessGame;

public class LoadGame extends ServerMessage {

    private final ChessGame game;
    private final String teamColor;

    public LoadGame(ChessGame game, String teamColor) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
        this.teamColor = teamColor;
    }

    public ChessGame getGame() {return this.game;}

    public String getTeamColor() {return this.teamColor;}
}
