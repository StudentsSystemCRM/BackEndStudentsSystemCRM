package edutrack.emailService.exception;

public class TriggerNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public TriggerNotFoundException(String message) {
        super(message);
    }
}
