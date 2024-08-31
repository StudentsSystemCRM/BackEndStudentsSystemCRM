package edutrack.dto.request.students;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import static edutrack.constant.ValidAccountConstant.NAME_PATTERN;
import static edutrack.constant.ValidationAccountingMessage.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

import edutrack.constant.GroupStatus;
import edutrack.entity.students.Student;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupUpdateDataRequest {
	@NotNull(message = "Name cannot be null.")
	@Pattern(regexp = NAME_PATTERN, message = INVALID_NAME)
	String name;

    String whatsApp;
    String skype;
    String slack;
    GroupStatus status;
    LocalDate startDate;
    LocalDate expFinishDate;
    List<DayOfWeek> lessons;
    List<DayOfWeek> webinars;
    Boolean DeactivateAfter30Days;
    List<Student> students;
}
