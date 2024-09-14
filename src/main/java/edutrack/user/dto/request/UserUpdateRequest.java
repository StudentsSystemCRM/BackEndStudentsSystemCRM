package edutrack.user.dto.request;

import java.time.LocalDate;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import edutrack.user.dto.validation.MultiFormatLocalDateDeserializer;
import edutrack.user.dto.validation.ValidRangeDate;
import edutrack.user.constant.ValidAccountConstant;
import edutrack.user.constant.ValidationAccountingMessage;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
@With
public class UserUpdateRequest {
	@NotNull(message = ValidationAccountingMessage.NULL_EMAIL)
	@NotBlank(message = ValidationAccountingMessage.BLANK_EMAIL)
	@Email(message = ValidationAccountingMessage.INVALID_EMAIL)
	String email; 
	
	@NotNull(message = ValidationAccountingMessage.NULL_NAME)
	@Pattern(regexp = ValidAccountConstant.NAME_PATTERN, message = ValidationAccountingMessage.INVALID_NAME)
	String name;
	
	@NotNull(message = ValidationAccountingMessage.NULL_NAME)
	@Pattern(regexp = ValidAccountConstant.NAME_PATTERN, message = ValidationAccountingMessage.INVALID_NAME)
	String surname;
	
	@NotNull(message = ValidationAccountingMessage.NULL_PHONE)
	@Pattern(regexp = ValidAccountConstant.PHONE_NUMBER_PATTERN, message = ValidationAccountingMessage.INVALID_PHONE)
	String phone;
	
	@JsonDeserialize(using = MultiFormatLocalDateDeserializer.class)
	@ValidRangeDate(yearsFromTodayToFuture = 0)
	LocalDate birthdate;
}
