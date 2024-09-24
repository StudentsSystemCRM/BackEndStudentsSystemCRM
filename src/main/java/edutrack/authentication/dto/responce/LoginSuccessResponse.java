package edutrack.authentication.dto.responce;

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
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
@With
@ToString
public class LoginSuccessResponse {
	String token;
	String refreshToken;
	String name;
	String surname;
	String phone;
	LocalDate birthdate;
	LocalDate createdDate;
	Set<Role> roles;
}
