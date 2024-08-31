package edutrack.dto.response.students;

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
public class StudentDataResponse {
	Long id;
	String name;
	String surname;
	String phone;
	String email;
	String city;
	String course;
	String source;
	LeadStatus leadStatus;
}
