package edutrack.lecturer.dto.request;

import edutrack.lecturer.constant.LecturerStatus;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

import static edutrack.user.constant.ValidAccountConstant.*;
import static edutrack.user.constant.ValidationAccountingMessage.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LecturerCreateRequest {

    @NotBlank(message = NULL_NAME)
    @Pattern(regexp = NAME_PATTERN, message = INVALID_NAME)
    private String firstName;

    @NotBlank(message = NULL_NAME)
    @Pattern(regexp = NAME_PATTERN, message = INVALID_NAME)
    private String lastName;

    @NotBlank(message = NULL_PHONE)
    @Pattern(regexp = PHONE_NUMBER_PATTERN, message = INVALID_PHONE)
    private String phoneNumber;

    @NotBlank(message = BLANK_EMAIL)
    @Pattern(regexp = EMAIL_PATTERN, message = INVALID_EMAIL)
    private String email;

    @NotBlank(message = "City is mandatory")
    @Pattern(regexp = CITY_PATTERN, message = INVALID_CITY)
    private String city;

    @NotNull(message = "Status is mandatory")
    private LecturerStatus status = LecturerStatus.ACTIVE;

    private Set<String> groups = new HashSet<>();
}
