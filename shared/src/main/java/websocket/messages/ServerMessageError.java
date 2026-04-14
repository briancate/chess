package websocket.messages;

public class ServerMessageError extends ServerMessage {

    private final String message;

    public ServerMessageError(String message) {
        super(ServerMessageType.ERROR);
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
