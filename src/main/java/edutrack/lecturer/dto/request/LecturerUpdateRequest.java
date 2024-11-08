package edutrack.lecturer.dto.request;

import edutrack.lecturer.constant.LecturerStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Set;
import static edutrack.user.constant.ValidAccountConstant.*;
import static edutrack.user.constant.ValidationAccountingMessage.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LecturerUpdateRequest {
    @NotNull
    private Long id;

    @Pattern(regexp = NAME_PATTERN, message = INVALID_NAME)
    private String firstName;

    @Pattern(regexp = NAME_PATTERN, message = INVALID_NAME)
    private String lastName;

    @Pattern(regexp = PHONE_NUMBER_PATTERN, message = INVALID_PHONE)
    private String phoneNumber;

    @Pattern(regexp = EMAIL_PATTERN, message = INVALID_EMAIL)
    private String email;

    @Pattern(regexp = CITY_PATTERN, message = "Invalid city")
    private String city;

    private LecturerStatus status;

    private Set<Long> groupIds;
}
