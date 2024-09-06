package edutrack.exception;

@SuppressWarnings("serial")
public class JwtTokenMissingException extends  RuntimeException {
    public JwtTokenMissingException(String message) {
        super(message);
    }
}
