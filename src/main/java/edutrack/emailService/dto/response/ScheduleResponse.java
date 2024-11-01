package edutrack.emailService.dto.response;

import edutrack.emailService.dto.TemplateEmailDetails;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.ZonedDateTime;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ScheduleResponse {
    String jobId;
    String triggerId;
    ZonedDateTime triggerTime;
    List<TemplateEmailDetails> templateEmailDetailsList;
}