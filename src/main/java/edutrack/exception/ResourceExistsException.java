package edutrack.exception;

@SuppressWarnings("serial")
public class ResourceExistsException extends RuntimeException {
	
	public ResourceExistsException(String message) {
		super(message);
	}
}
