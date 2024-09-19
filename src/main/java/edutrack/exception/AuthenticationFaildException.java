package edutrack.exception;


@SuppressWarnings("serial")
public class AuthenticationFaildException extends RuntimeException{
	public AuthenticationFaildException(String message) {
		super(message);
	}
}