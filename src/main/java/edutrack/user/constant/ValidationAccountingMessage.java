package edutrack.user.constant;

public interface ValidationAccountingMessage {
	String INVALID_PASSWORD_CONTAIN = "Password  must contain:  "
			+ " - At least one digit, "
			+ " - At least one lowercase letter, "
			+ "	- At least one special character (@#$%^&+=), "
			+ "	- No whitespace, "
			+ "	- Minimum 8 characters";
	String NULL_PASSWORD = "Password cannot be null";
	String BLANK_PASSWORD = "Password cannot be blank";

	String INVALID_EMAIL = "Invalid email";	
	String NULL_EMAIL = "Email cannot be null";
	String BLANK_EMAIL = "Email cannot be blank";
	
	String NULL_NAME = "name cannot be null";
	String INVALID_NAME = "Name can contain:  "
			+ "- English, Russian, or Hebrew letters, "
			+ "- Hyphens, spaces, and apostrophes, "
			+ "- Minimum 1 character and maximum 50 characters";
	
	String NULL_PHONE = "phone cannot be null";
	String INVALID_PHONE = "Phone number can contain:  "
			+ "Optional leading + symbol, "
			+ "Digits, hyphens, spaces, and parentheses, "
			+ "Minimum 7 characters and maximum 20 characters";
}
