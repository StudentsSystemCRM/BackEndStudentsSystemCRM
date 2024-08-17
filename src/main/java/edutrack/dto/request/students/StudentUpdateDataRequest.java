package edutrack.dto.request.students;

import jakarta.validation.constraints.*;

import lombok.*;
import lombok.experimental.FieldDefaults;

import static edutrack.constant.ValidAccountConstant.NAME_PATTERN;
import static edutrack.constant.ValidAccountConstant.PHONE_NUMBER_PATTERN;
import static edutrack.constant.ValidationAccountingMessage.*;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentUpdateDataRequest {
	Long id;
	String name;
	String surname;
	String phone;
	String email;
	String city;
	String course;
	String source;
	String leadStatus;
}
