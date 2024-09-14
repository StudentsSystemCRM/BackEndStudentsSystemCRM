package edutrack.reminder.dto.request;

import java.time.LocalDateTime;

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
public class AddGroupScheduleRequest {
	@NotNull(message = "Group name cannot be null.")
	String name;

	LocalDateTime dateTime;
	
	@NotNull(message = "Comment cannot be null")
	@NotBlank(message = "Comment cannot be blank")
	String comment;
}
