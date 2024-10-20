package edutrack.group.dto.request;

import static edutrack.user.constant.ValidationAccountingMessage.INVALID_NAME;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
	@Pattern(regexp = "^[a-zA-Zа-яА-ЯёЁ0-9א-ת\\-\\s']{1,50}$", message = INVALID_NAME)
    String name;

    String whatsApp;
    String skype;
    String slack;
    LocalDate startDate;
    LocalDate expFinishDate;
    List<LocalDateTime> lessonsDays;
    List<LocalDateTime> webinarsDays;
}
