package edutrack.dto.response.accounting;

import java.time.LocalDate;
import java.util.Set;

public class UserDataResponse {
	
	String email; //login
	String name;
	String surname;
	String phone;
	LocalDate birthdate;
	LocalDate createdDate;
	Set<Role> roles;

}
