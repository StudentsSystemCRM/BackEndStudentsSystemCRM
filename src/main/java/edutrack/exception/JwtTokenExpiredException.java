package edutrack.exception;

public class JwtTokenExpiredException extends  RuntimeException {
    public JwtTokenExpiredException(String message) {
        super(message);
    }
}
