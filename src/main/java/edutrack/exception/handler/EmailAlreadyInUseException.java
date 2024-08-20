package edutrack.exception;

public class EmailAlreadyInUseException extends ResourceExistsException {
    public EmailAlreadyInUseException(String message) {
        super(message);
    }
}
