package edutrack.user.exception;

@SuppressWarnings("serial")
public class AccessException extends RuntimeException {
    public AccessException(String message) {
        super(message);
    }
}
