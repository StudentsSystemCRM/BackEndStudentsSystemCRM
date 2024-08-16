package edutrack.constant;

public interface ValidAccountConstant {
	
	// Password must contain:
	// - At least one digit
	// - At least one lowercase letter
	// - At least one uppercase letter
	// - At least one special character (@#$%^&+=)
	// - No whitespace
	// - Minimum 8 characters
	String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
	
	
	// Name can contain:
	// - English, Russian, or Hebrew letters
	// - Hyphens, spaces, and apostrophes
	// - Minimum 1 character and maximum 50 characters
	String NAME_PATTERN = "^[a-zA-Zа-яА-ЯёЁא-ת\\-\\s']{1,50}$";
	
	// Phone number can contain:
	// - Optional leading + symbol
	// - Digits, hyphens, spaces, and parentheses
	// - Minimum 7 characters and maximum 20 characters
	String PHONE_NUMBER_PATTERN ="^\\+?[0-9\\-\\s()]{7,20}$";

}
