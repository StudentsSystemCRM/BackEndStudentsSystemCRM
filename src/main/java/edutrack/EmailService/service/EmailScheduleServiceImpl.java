package edutrack.EmailService.service;

import edutrack.EmailService.dto.ScheduleRequest;
import edutrack.EmailService.dto.TemplateEmailDetails;
import edutrack.EmailService.dto.response.ScheduleResponse;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class EmailScheduleServiceImpl implements EmailSchedulerService {
    private final Scheduler scheduler;

    @Autowired
    public EmailScheduleServiceImpl(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    static final String TEMPLATE_EMAIL_DATA_LIST = "templateEmailDetailsList";

    @Override
    public ScheduleResponse schedulerEmailJob(ScheduleRequest scheduleRequest) throws SchedulerException {
        JobDetail jobDetail = buildJobDetail(scheduleRequest.getTemplateEmailDetailsList());
        Trigger trigger = buildJobTrigger(jobDetail, scheduleRequest.getTriggerTime());
        try {
            scheduler.scheduleJob(jobDetail, trigger);
            return new ScheduleResponse(jobDetail.getKey().getName(), trigger.getKey().getName(),
                    scheduleRequest.getTriggerTime(), scheduleRequest.getTemplateEmailDetailsList());
        } catch (SchedulerException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Trigger buildJobTrigger(JobDetail jobDetail, ZonedDateTime triggerTime) {
        String triggerId = UUID.randomUUID().toString();
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(triggerId, "emailTriggers")
                .startAt(Date.from(triggerTime.toInstant()))
                .withSchedule(SimpleScheduleBuilder.simpleSchedule())
                .build();

    }

    private JobDetail buildJobDetail(List<TemplateEmailDetails> templateEmailDetailsList) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(TEMPLATE_EMAIL_DATA_LIST, templateEmailDetailsList);
        String jobId = UUID.randomUUID().toString();

        return JobBuilder.newJob(EmailJob.class)
                .withIdentity(jobId, "emailJobs")
                .usingJobData(jobDataMap)
                .storeDurably()
                .build();
    }

}
