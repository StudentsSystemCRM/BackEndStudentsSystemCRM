package edutrack.EmailService.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ScheduleRequest {
    String jobId;
    ZonedDateTime triggerTime;
    List<TemplateEmailDetails> templateEmailDetailsList;

    public ScheduleRequest(ZonedDateTime triggerTime, List<TemplateEmailDetails> templateEmailDetailsList) {
        this.jobId = UUID.randomUUID().toString();
        this.triggerTime = triggerTime;
        this.templateEmailDetailsList = templateEmailDetailsList;
    }
}
