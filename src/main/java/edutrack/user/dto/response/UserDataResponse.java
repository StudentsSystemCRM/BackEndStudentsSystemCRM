package edutrack.user.dto.response;

import java.time.LocalDate;
import java.util.Set;

import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
@With
public class UserDataResponse {
	String email; //login
	String name;
	String surname;
	LocalDate birthdate;
	String phone;

	LocalDate createdDate;
	Set<Role> roles;
}
