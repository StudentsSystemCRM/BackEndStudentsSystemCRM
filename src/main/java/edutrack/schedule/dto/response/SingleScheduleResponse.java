package edutrack.schedule.dto.response;

import java.time.LocalDateTime;

import edutrack.schedule.constant.SheduleType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SingleScheduleResponse {
	Long id;
	SheduleType sheduleType;
    LocalDateTime sendDate;
    String subject;
    String message;
}
