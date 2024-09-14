package edutrack.user.dto.request;

import edutrack.user.constant.ValidAccountConstant;
import edutrack.user.constant.ValidationAccountingMessage;
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
public class PasswordUpdateRequest {
	@NotNull(message = ValidationAccountingMessage.NULL_PASSWORD)
	@Pattern(regexp = ValidAccountConstant.PASSWORD_PATTERN, message = ValidationAccountingMessage.INVALID_PASSWORD_CONTAIN)
	String password;
}
