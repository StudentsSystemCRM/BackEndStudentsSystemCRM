package edutrack.lecturer.dto.request;

import edutrack.group.entity.GroupEntity;
import edutrack.lecturer.constant.LecturerStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LecturerCreateRequest {
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;
    private String city;
    private LecturerStatus status;
    private Set<GroupEntity> groups;
}
