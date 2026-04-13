package websocket.commands;

public class ConnectCommand extends UserGameCommand {

    private final String teamColor;

    public ConnectCommand(CommandType commandType, String authToken, Integer gameID, String teamColor) {
        super(commandType, authToken, gameID);
        this.teamColor = teamColor;
    }

    public String getTeamColor() {
        return this.teamColor;
    }
}
