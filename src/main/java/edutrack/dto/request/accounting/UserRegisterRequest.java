package edutrack.dto.request.accounting;

import java.time.LocalDate;

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
	
	String email; //login
	String password; //save by hach bcrypt in db
	String name;
	String surname;
	String phone;
	LocalDate birthdate;

}
