package edutrack.dto.request.accounting;

import java.time.LocalDate;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import edutrack.constant.ValidAccountConstant;
import edutrack.validation.MultiFormatLocalDateDeserializer;
import edutrack.validation.ValidRangeDate;

import static edutrack.constant.ValidationAccountingMessage.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserRegisterRequest {
	@NotNull(message = NULL_EMAIL)
	@NotBlank(message = BLANK_EMAIL)
	@Email(message = INVALID_EMAIL)
	String email; //login
	
	@NotNull(message = NULL_PASSWORD)
	@Pattern(regexp = ValidAccountConstant.PASSWORD_PATTERN, message = INVALID_PASSWORD_CONTAIN)
	String password; 
	
	@NotNull(message = NULL_NAME)
	@Pattern(regexp = ValidAccountConstant.NAME_PATTERN, message = INVALID_NAME)
	String name;
	
	@NotNull(message = NULL_NAME)
	@Pattern(regexp = ValidAccountConstant.NAME_PATTERN, message = INVALID_NAME)
	String surname;
	
	@NotNull(message = NULL_PHONE)
	@Pattern(regexp = ValidAccountConstant.PHONE_NUMBER_PATTERN, message = INVALID_PHONE)
	String phone;
	
	@JsonDeserialize(using = MultiFormatLocalDateDeserializer.class)
	@ValidRangeDate(yearsfromTodaytoFuture = 0, yearsfromTodaytoPast = 120)
	LocalDate birthdate;
}
