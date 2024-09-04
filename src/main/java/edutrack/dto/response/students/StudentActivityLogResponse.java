package edutrack.dto.response.students;

import java.util.List;

import edutrack.constant.LeadStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentActivityLogResponse {
	Long id;
	String name;
	String surname;
	String phone;
	String email;
	String city;
	String course;
	String source;
	LeadStatus leadStatus;
	List<StudentActivityLog> activityLog;
}
