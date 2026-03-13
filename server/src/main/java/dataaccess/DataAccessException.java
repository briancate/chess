package dataaccess;

/**
 * Indicates there was an error connecting to the database (I use it incorrectly as 400 level errors)
 */
public class DataAccessException extends Exception{
    public DataAccessException(String message) {
        super(message);
    }
    public DataAccessException(String message, Throwable ex) {
        super(message, ex);
    }
}
