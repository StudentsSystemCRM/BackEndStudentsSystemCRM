package edutrack.modul.user.dto.request;

import static edutrack.constant.ValidAccountConstant.NAME_PATTERN;
import static edutrack.constant.ValidAccountConstant.PHONE_NUMBER_PATTERN;
import static edutrack.constant.ValidationAccountingMessage.BLANK_EMAIL;
import static edutrack.constant.ValidationAccountingMessage.INVALID_EMAIL;
import static edutrack.constant.ValidationAccountingMessage.INVALID_NAME;
import static edutrack.constant.ValidationAccountingMessage.INVALID_PHONE;
import static edutrack.constant.ValidationAccountingMessage.NULL_EMAIL;
import static edutrack.constant.ValidationAccountingMessage.NULL_NAME;
import static edutrack.constant.ValidationAccountingMessage.NULL_PHONE;

import java.time.LocalDate;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import edutrack.validation.MultiFormatLocalDateDeserializer;
import edutrack.validation.ValidRangeDate;
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
	@NotNull(message = NULL_EMAIL)
	@NotBlank(message = BLANK_EMAIL)
	@Email(message = INVALID_EMAIL)
	String email; 
	
	@NotNull(message = NULL_NAME)
	@Pattern(regexp = NAME_PATTERN, message = INVALID_NAME)
	String name;
	
	@NotNull(message = NULL_NAME)
	@Pattern(regexp = NAME_PATTERN, message = INVALID_NAME)
	String surname;
	
	@NotNull(message = NULL_PHONE)
	@Pattern(regexp = PHONE_NUMBER_PATTERN, message = INVALID_PHONE)
	String phone;
	
	@JsonDeserialize(using = MultiFormatLocalDateDeserializer.class)
	@ValidRangeDate(yearsFromTodayToFuture = 0)
	LocalDate birthdate;
}
