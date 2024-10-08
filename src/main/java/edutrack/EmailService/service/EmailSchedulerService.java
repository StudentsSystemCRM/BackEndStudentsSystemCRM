package edutrack.EmailService.service;

import edutrack.EmailService.dto.ScheduleRequest;
import edutrack.EmailService.dto.TemplateEmailDetails;
import edutrack.EmailService.dto.response.ScheduleResponse;
import org.quartz.SchedulerException;

import java.time.ZonedDateTime;
import java.util.List;

public interface EmailSchedulerService {

    ScheduleResponse schedulerEmailJob(ScheduleRequest scheduleRequest) throws SchedulerException;
}
