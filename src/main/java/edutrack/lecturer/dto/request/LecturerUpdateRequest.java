package edutrack.lecturer.dto.request;

import edutrack.lecturer.constant.LecturerStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LecturerUpdateRequest {

    @NotNull(message = "ID cannot be null.")
    private Long id;

    @Size(max = 50, message = "First name cannot be longer than 50 characters")
    private String firstName;

    @Size(max = 50, message = "Last name cannot be longer than 50 characters")
    private String lastName;

    @Size(max = 15, message = "Phone number cannot be longer than 15 characters")
    private String phoneNumber;

    @Email(message = "Email should be valid")
    private String email;

    @Size(max = 100, message = "City cannot be longer than 100 characters")
    private String city;

    private LecturerStatus status;

    private Set<String> groups = new HashSet<>();
}
