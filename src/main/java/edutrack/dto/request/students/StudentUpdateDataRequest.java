package edutrack.dto.request.students;

import edutrack.constant.ValidAccountConstant;
import edutrack.constant.ValidationAccountingMessage;

import jakarta.validation.constraints.*;

import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentUpdateDataRequest {
	@NotNull(message = "ID cannot be null")
	Long id;

	@NotBlank(message = ValidationAccountingMessage.NULL_NAME)
	@Pattern(regexp = ValidAccountConstant.NAME_PATTERN, message = ValidationAccountingMessage.INVALID_NAME)
	String name;

	@NotBlank(message = ValidationAccountingMessage.NULL_NAME)
	@Pattern(regexp = ValidAccountConstant.NAME_PATTERN, message = ValidationAccountingMessage.INVALID_NAME)
	String surname;

	@NotBlank(message = ValidationAccountingMessage.NULL_PHONE)
	@Pattern(regexp = ValidAccountConstant.PHONE_NUMBER_PATTERN, message = ValidationAccountingMessage.INVALID_PHONE)
	String phone;

	@NotBlank(message = ValidationAccountingMessage.BLANK_EMAIL)
	@Email(message = ValidationAccountingMessage.INVALID_EMAIL)
	String email;

	String city;
	String course;
	String source;
	String leadStatus;
}
