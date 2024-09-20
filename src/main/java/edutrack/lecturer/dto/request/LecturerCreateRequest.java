package edutrack.lecturer.dto.request;

import edutrack.lecturer.constant.LecturerStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LecturerCreateRequest { @NotBlank(message = "First name is mandatory")
@Size(max = 50, message = "First name cannot be longer than 50 characters")
private String firstName;

    @NotBlank(message = "Last name is mandatory")
    @Size(max = 50, message = "Last name cannot be longer than 50 characters")
    private String lastName;

    @NotBlank(message = "Phone number is mandatory")
    @Size(max = 15, message = "Phone number cannot be longer than 15 characters")
    private String phoneNumber;

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "City is mandatory")
    @Size(max = 100, message = "City cannot be longer than 100 characters")
    private String city;

    @NotNull(message = "Status is mandatory")
    private LecturerStatus status = LecturerStatus.ACTIVE;

    private Set<String> groups = new HashSet<>();

}
