package edutrack.security.util;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Utils {
	public static String getJsonFromObjectOrEmtyString(Object object) {
		try {
			return new ObjectMapper().writeValueAsString(object);
		} catch (Exception e) {
			return "";
		}
	}

}
