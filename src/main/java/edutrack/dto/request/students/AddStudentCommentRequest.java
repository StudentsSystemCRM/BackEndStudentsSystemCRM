package edutrack.dto.request.students;

import java.time.LocalDate;

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
public class AddStudentCommentRequest {
	@NotNull(message = "ID cannot be null.")
	Long studentId;

	LocalDate date;
	String message;
}
