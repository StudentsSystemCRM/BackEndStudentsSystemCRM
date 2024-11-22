package edutrack.schedule.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SheduleType {
	    LOCAL("Local"),
	    SMS("Sms"),
	    TELEGRAM("Telegram");

	    private final String sheduleType;
}
