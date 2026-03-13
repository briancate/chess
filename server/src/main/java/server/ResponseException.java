package server;

/**
 * Indicates a 500 level error currently, should be 400 level someday
 */
public class ResponseException extends Exception {
//    private final int httpCode;
//    public ResponseException(String message, int httpCode) {
//        this.httpCode = httpCode;
//        super(message);
//    }
//    public ResponseException(String message, Throwable ex, int httpCode) {
//        this.httpCode = httpCode;
//        super(message, ex);
//    }

    public ResponseException(String message) {super(message);}
    public ResponseException(String message, Throwable ex) {super(message, ex);}
}
