package edutrack.dto.request.students;

import edutrack.constant.ValidAccountConstant;
import edutrack.constant.ValidationAccountingMessage;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentCreateRequest {
	@NotBlank(message = "Name cannot be blank")
	@Pattern(regexp = ValidAccountConstant.NAME_PATTERN, message = ValidationAccountingMessage.INVALID_NAME)
	String name;

	@NotBlank(message = "Surname cannot be blank")
	@Pattern(regexp = ValidAccountConstant.NAME_PATTERN, message = ValidationAccountingMessage.INVALID_NAME)
	String surname;

	@NotBlank(message = "Phone cannot be blank")
	@Pattern(regexp = ValidAccountConstant.PHONE_NUMBER_PATTERN, message = ValidationAccountingMessage.INVALID_PHONE)
	String phone;

	@NotBlank(message = "Email cannot be blank")
	@Email(message = ValidationAccountingMessage.INVALID_EMAIL)
	String email;

	String city;
	String course;
	String source;
	String leadStatus;
	String comment;
}
