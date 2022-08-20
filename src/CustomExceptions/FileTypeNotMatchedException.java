package CustomExceptions;

public class FileTypeNotMatchedException extends Exception {
    public FileTypeNotMatchedException(String message) {
        super(message);
    }
}
