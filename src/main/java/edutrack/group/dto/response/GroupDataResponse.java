package edutrack.group.dto.response;

import java.time.LocalDate;
import java.util.List;

import edutrack.group.constant.GroupStatus;
import edutrack.group.constant.WeekDay;
import edutrack.reminder.entity.GroupScheduleEntity;
import edutrack.student.entity.StudentEntity;
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
    private List<StudentEntity> students;
    private List<GroupScheduleEntity> groupReminders;
}
