package CustomExceptions;

public class ConnectionNotMadeException extends Exception {
    public ConnectionNotMadeException(String message) {
        super(message);
    }
}
