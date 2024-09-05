package edutrack.dto.request.group;

import static edutrack.constant.ValidAccountConstant.NAME_PATTERN;
import static edutrack.constant.ValidationAccountingMessage.INVALID_NAME;

import java.time.LocalDate;
import java.util.List;

import edutrack.constant.WeekDay;
import edutrack.entity.students.Student;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupCreateRequest {
	@NotNull(message = "Name cannot be null.")
	@Pattern(regexp = NAME_PATTERN, message = INVALID_NAME)
    String name;

    String whatsApp;
    String skype;
    String slack;
    LocalDate startDate;
    LocalDate expFinishDate;
    List<WeekDay> lessonsDays;
    List<WeekDay> webinarsDays;
    List<Student> students;
}
