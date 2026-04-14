package websocket.messages;

public class ServerMessageError extends ServerMessage {

    private final String errorMessage;

    public ServerMessageError(String message) {
        super(ServerMessageType.ERROR);
        this.errorMessage = message;
    }

    public String getMessage() {
        return this.errorMessage;
    }
}
