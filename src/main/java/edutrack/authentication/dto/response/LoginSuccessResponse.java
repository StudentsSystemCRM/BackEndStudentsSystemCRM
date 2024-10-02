package edutrack.authentication.dto.response;

import java.time.LocalDate;
import java.util.Set;

import edutrack.user.dto.response.Role;
import lombok.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class LoginSuccessResponse {
	String name;
	String surname;
	LocalDate birthdate;
	String phone;

	LocalDate createdDate;
	Set<Role> roles;

	String accessToken;
	String refreshToken;
}
