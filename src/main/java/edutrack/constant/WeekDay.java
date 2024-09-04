package edutrack.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WeekDay {
	   SUNDAY("Sunday"),
	   MONDAY("Monday"),
	   TUESDAY("Tuesday"),
	   WEDNESDAY("Wednesday"),
	   THURSDAY("Thursday"),
	   FRIDAY("Friday"),
	   SATURDAY("Saturday");
	   
	   private final String weekDay;
	}
