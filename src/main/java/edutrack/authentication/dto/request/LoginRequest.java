package edutrack.authentication.dto.request;

import edutrack.user.constant.ValidationAccountingMessage;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class LoginRequest {
    @NotNull(message = ValidationAccountingMessage.NULL_EMAIL)
    @NotBlank(message = ValidationAccountingMessage.BLANK_EMAIL)
    @Email(message = ValidationAccountingMessage.INVALID_EMAIL)
    String email;

    @NotNull(message = ValidationAccountingMessage.NULL_PASSWORD)
    @NotBlank(message = ValidationAccountingMessage.BLANK_PASSWORD)
    String password;
}
