package edutrack.schedule.dto.request;

import java.time.ZonedDateTime;

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
public class ScheduleCreateRequest {
	@NotNull(message = "ID cannot be null.")
	Long id;

	SheduleType sheduleType;
	@NotNull(message = "SendTime cannot be null.")
	ZonedDateTime sendDate;
    String subject;
	
	@NotNull(message = "Message cannot be null")
	@NotBlank(message = "Message cannot be blank")
    String message;
}
