package edutrack.group.dto.response;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import edutrack.group.constant.GroupStatus;
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
	Long id;
    String name;
    String whatsApp;
    String skype;
    String slack;
    GroupStatus status;
    LocalDate startDate;
    LocalDate expFinishDate;
    List<ZonedDateTime> lessonsDays;
    List<ZonedDateTime> webinarsDays;
    Boolean deactivateAfter30Days;
}
