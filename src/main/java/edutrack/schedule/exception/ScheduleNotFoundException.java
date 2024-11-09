package edutrack.schedule.exception;

@SuppressWarnings("serial")
public class ScheduleNotFoundException  extends RuntimeException {
	
	public ScheduleNotFoundException(String message) {
		super(message);
	}
}
