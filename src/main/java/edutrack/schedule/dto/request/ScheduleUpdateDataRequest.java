package edutrack.schedule.dto.request;

import java.time.LocalDateTime;

import edutrack.schedule.constant.SheduleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleUpdateDataRequest {
	@NotNull(message = "ID cannot be null.")
	Long id;
	
	@NotNull(message = "Shedule ID cannot be null.")
	Long scheduleId;
	SheduleType sheduleType;
    LocalDateTime sendDate;
    String subject;
	
	@NotNull(message = "Message cannot be null")
	@NotBlank(message = "Message cannot be blank")
    String message;
}
