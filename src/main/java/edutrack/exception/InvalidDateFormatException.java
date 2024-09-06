package edutrack.exception;

@SuppressWarnings("serial")
public class InvalidDateFormatException extends RuntimeException {
    public InvalidDateFormatException(String message) {
        super(message);
    }
}
