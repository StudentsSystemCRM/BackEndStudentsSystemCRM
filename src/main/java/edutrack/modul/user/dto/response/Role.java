package edutrack.modul.user.dto.response;

import java.util.Arrays;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Role {
    USER("USER"),
    ADMIN("ADMIN"),
    CEO("CEO");

    private final String value;

    public static boolean existsByValue(String role) {
        boolean res = false;
        if (role != null && !role.isBlank()) {
            res = Arrays.stream(values())
                    .anyMatch(e -> e.value.equalsIgnoreCase(role));
        }
        return res;
    }

    public static Role fromValue(String role) {
        if (role != null && !role.isBlank()) {
            for (Role r : values()) {
                if (r.value.equalsIgnoreCase(role)) {
                    return r;
                }
            }
        }
        throw new IllegalArgumentException("No enum constant for value: " + role);
    }
}
