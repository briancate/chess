package dataaccess;

/**
 * Indicates there was an error connecting to the database (I use it incorrectly as 400 level errors)
 */
public class DataAccessException extends Exception{
//    private final int httpCode;
    public DataAccessException(String message) {
//        httpCode = 500;
        super(message);
    }
    public DataAccessException(String message, Throwable ex) {
//        httpCode = 500;
        super(message, ex);
    }
}
