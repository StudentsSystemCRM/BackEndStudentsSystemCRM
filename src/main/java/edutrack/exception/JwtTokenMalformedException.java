package edutrack.exception;

@SuppressWarnings("serial")
public class JwtTokenMalformedException extends RuntimeException {
    public JwtTokenMalformedException(String message) {
        super(message);
    }
}
