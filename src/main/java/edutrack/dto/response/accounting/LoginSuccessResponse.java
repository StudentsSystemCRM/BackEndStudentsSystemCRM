package edutrack.dto.response.accounting;

import java.time.LocalDate;
import java.util.Set;

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
public class LoginSuccessResponse {
	
	String token;
	String name;
	String surname;
	String phone;
	LocalDate birthdate;
	LocalDate createdDate;
	Set<Role> roles;
	

}
