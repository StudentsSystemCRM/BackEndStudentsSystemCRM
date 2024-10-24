package edutrack.emailService.service;

import edutrack.emailService.dto.request.RescheduleRequest;
import edutrack.emailService.dto.request.ScheduleRequest;
import edutrack.emailService.dto.response.ScheduleResponse;
import org.quartz.SchedulerException;

import java.util.List;

public interface EmailSchedulerService {

    ScheduleResponse scheduleEmailJob(ScheduleRequest scheduleRequest) throws SchedulerException;
    boolean rescheduleEmailJob(RescheduleRequest rescheduleRequest) throws SchedulerException;

    boolean cancelEmailJob (String jobId) throws SchedulerException;
    List<ScheduleResponse> getAllScheduledEmailJobs() throws SchedulerException;
}