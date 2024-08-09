package edutrack.dto.request.accounting;

import java.time.LocalDate;

public class UserRegisterRequest {
	
	String email; //login
	String password; //save by hach bcrypt in db
	String name;
	String surname;
	String phone;
	LocalDate birthdate;

}
