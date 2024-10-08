package edutrack.EmailService.dto.response;

import edutrack.EmailService.dto.TemplateEmailDetails;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.ZonedDateTime;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ScheduleResponse {
    String jobId;
    String triggerId;
    ZonedDateTime triggerTime;
    List<TemplateEmailDetails> templateEmailDetailsList;
}
