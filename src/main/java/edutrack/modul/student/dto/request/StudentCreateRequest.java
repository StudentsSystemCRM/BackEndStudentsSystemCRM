package edutrack.modul.student.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
public class StudentCreateRequest {
	@NotNull(message = "Name cannot be null.")
	@Pattern(regexp = NAME_PATTERN, message = INVALID_NAME)
	String name;

	@NotNull(message = "Surname cannot be null.")
	@Pattern(regexp = NAME_PATTERN, message = INVALID_NAME)
	String surname;

	@NotNull(message = "Phone number cannot be null.")
	@Pattern(regexp = PHONE_NUMBER_PATTERN, message = INVALID_PHONE)
	String phone;

	@NotBlank(message = "Email cannot be blank.")
	@Email(message = INVALID_EMAIL)
	String email;

	String city;
	String course;
	String source;
	LeadStatus leadStatus;
	String comment;
}
