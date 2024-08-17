package edutrack.dto.request.accounting;

import static edutrack.constant.ValidationAccountingMessage.INVALID_PASSWORD_CONTAIN;
import static edutrack.constant.ValidationAccountingMessage.NULL_PASSWORD;

import edutrack.constant.ValidAccountConstant;
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
	@NotNull(message = NULL_PASSWORD)
	@Pattern(regexp = ValidAccountConstant.PASSWORD_PATTERN, message = INVALID_PASSWORD_CONTAIN)
	String password;
}
