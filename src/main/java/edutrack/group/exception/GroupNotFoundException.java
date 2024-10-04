package edutrack.group.exception;

@SuppressWarnings("serial")
public class GroupNotFoundException extends RuntimeException {

	public GroupNotFoundException(String message) {
		super(message);
	}
}