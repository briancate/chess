package server;

/**
 * Indicates a 500 level error currently, should be 400 level someday
 */
public class ResponseException extends Exception {
    public ResponseException(String message) {super(message);}
    public ResponseException(String message, Throwable ex) {super(message, ex);}
}
