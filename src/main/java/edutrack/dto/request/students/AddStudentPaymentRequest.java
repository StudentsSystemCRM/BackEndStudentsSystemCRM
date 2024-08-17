package edutrack.dto.request.students;

import java.time.LocalDate;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddStudentPaymentRequest {
	Long studentId;
	LocalDate date;
	String type;
	Integer amount;
	String details;
}
