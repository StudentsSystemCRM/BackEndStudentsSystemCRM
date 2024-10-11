package edutrack.lecturer.dto.response;

import edutrack.lecturer.constant.LecturerStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Set;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LecturerDataResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;
    private String city;
    private LecturerStatus status;
    private Set<Long> groupIds;
}
