package edutrack.modul.activityLog.dto.request;

import java.time.LocalDate;

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
public class AddActivityLogRequest {
	@NotNull(message = "ID cannot be null.")
	Long studentId;

	LocalDate date;
	
	@NotNull(message = "message cannot be null")
	@NotBlank(message = "message cannot be blank")
	String message;
}
