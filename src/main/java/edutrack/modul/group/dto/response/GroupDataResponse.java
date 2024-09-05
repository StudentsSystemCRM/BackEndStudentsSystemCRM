package edutrack.modul.group.dto.response;

import java.time.LocalDate;
import java.util.List;

import edutrack.constant.GroupStatus;
import edutrack.constant.WeekDay;
import edutrack.modul.reminder.entity.GroupReminders;
import edutrack.modul.student.entity.Student;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupDataResponse {
    private String name;
    private String whatsApp;
    private String skype;
    private String slack;
    private GroupStatus status;
    private LocalDate startDate;
    private LocalDate expFinishDate;
    private List<WeekDay> lessons;
    private List<WeekDay> webinars;
    private Boolean DeactivateAfter30Days;
    private List<Student> students;
    private List<GroupReminders> groupReminders;
}
