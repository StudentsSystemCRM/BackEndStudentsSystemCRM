package edutrack.group.dto.request;

import static edutrack.user.constant.ValidationAccountingMessage.INVALID_NAME;

import java.time.LocalDate;
import java.util.List;

import edutrack.group.constant.GroupStatus;
import edutrack.group.constant.WeekDay;
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
public class GroupUpdateDataRequest {
	@NotNull(message = "ID cannot be null.")
	Long id;
	@NotNull(message = "Name cannot be null.")
	@Pattern(regexp = "^[a-zA-Zа-яА-ЯёЁ0-9א-ת\\-\\s']{1,50}$", message = INVALID_NAME)
	String name;

    String whatsApp;
    String skype;
    String slack;
    GroupStatus status;
    LocalDate startDate;
    LocalDate expFinishDate;
    List<WeekDay> lessonsDays;
    List<WeekDay> webinarsDays;
    Boolean DeactivateAfter30Days;
}
