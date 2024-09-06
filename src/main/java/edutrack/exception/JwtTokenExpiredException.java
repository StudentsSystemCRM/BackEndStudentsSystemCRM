package edutrack.exception;

@SuppressWarnings("serial")
public class JwtTokenExpiredException extends  RuntimeException {
    public JwtTokenExpiredException(String message) {
        super(message);
    }
}
