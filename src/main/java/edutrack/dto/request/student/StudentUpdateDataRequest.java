package edutrack.dto.request.student;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import static edutrack.constant.ValidAccountConstant.NAME_PATTERN;
import static edutrack.constant.ValidAccountConstant.PHONE_NUMBER_PATTERN;
import static edutrack.constant.ValidationAccountingMessage.*;

import edutrack.constant.LeadStatus;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentUpdateDataRequest {
	@NotNull(message = "ID cannot be null.")
	Long id;

	@Pattern(regexp = NAME_PATTERN, message = INVALID_NAME)
	String name;

	@Pattern(regexp = NAME_PATTERN, message = INVALID_NAME)
	String surname;

	@Pattern(regexp = PHONE_NUMBER_PATTERN, message = INVALID_PHONE)
	String phone;

	@Email(message = INVALID_EMAIL)
	String email;

	String city;
	String course;
	String source;
	LeadStatus leadStatus;
}
