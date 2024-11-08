package edutrack.user.constant;

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

	String EMAIL_PATTERN = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";

// Email can contain:
// - English letters (both uppercase and lowercase)
// - Numbers
// - Dots (.), underscores (_), percent signs (%), plus signs (+), and hyphens (-)
// - Must contain a single '@' symbol
// - Domain must contain at least one dot ('.') with 2 to 6 letters in the top-level domain
String CITY_PATTERN = "^[A-Za-zÀ-ÿ'\\- ]{1,100}$";
// City can contain:
// - English letters (both uppercase and lowercase)
// - Letters with diacritical marks (e.g., accents, umlauts) for international cities
// - Hyphens (-), apostrophes ('), and spaces ( )
// - Must be between 1 and 100 characters in length


}
