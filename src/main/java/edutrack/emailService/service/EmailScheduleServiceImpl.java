package edutrack.emailService.service;

import edutrack.emailService.dto.request.RescheduleRequest;
import edutrack.emailService.dto.request.ScheduleRequest;
import edutrack.emailService.dto.TemplateEmailDetails;
import edutrack.emailService.dto.response.ScheduleResponse;
import edutrack.emailService.exception.TriggerNotFoundException;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.*;

@Service
public class EmailScheduleServiceImpl implements EmailSchedulerService {
    private final Scheduler scheduler;

    @Autowired
    public EmailScheduleServiceImpl(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    static final String TEMPLATE_EMAIL_DATA_LIST = "templateEmailDetailsList";
    final String EMAIL_JOB_GROUP = "emailJobs";
    final String EMAIL_TRIGGER_GROUP = "emailTriggers";

    @Override
    public ScheduleResponse scheduleEmailJob(ScheduleRequest scheduleRequest) throws SchedulerException {
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

    @Override
    public boolean rescheduleEmailJob(RescheduleRequest rescheduleRequest) throws SchedulerException {
        TriggerKey triggerKey = TriggerKey.triggerKey(rescheduleRequest.getTriggerId(), EMAIL_TRIGGER_GROUP);
        Trigger oldTrigger = scheduler.getTrigger(triggerKey);
        if (oldTrigger == null ) {
            throw new TriggerNotFoundException("No trigger found with trigger id: " + rescheduleRequest.getTriggerId());
        }
        Trigger newTrigger = TriggerBuilder.newTrigger()
                .forJob(oldTrigger.getJobKey())
                .withIdentity(triggerKey)
                .startAt(Date.from(rescheduleRequest.getNewTriggerTime().toInstant()))
                .withSchedule(SimpleScheduleBuilder.simpleSchedule())
                .build();

        scheduler.rescheduleJob(triggerKey, newTrigger);
        return true;
    }

    @Override
    public boolean cancelEmailJob(String jobId) throws SchedulerException {
        JobKey jobKey = JobKey.jobKey(jobId, EMAIL_JOB_GROUP);
        return scheduler.deleteJob(jobKey);

    }

    @Override
    public List<ScheduleResponse> getAllScheduledEmailJobs() throws SchedulerException {
        List<ScheduleResponse> scheduledJobs = new ArrayList<>();
        Set<JobKey> jobKeySet = scheduler.getJobKeys(GroupMatcher.jobGroupEquals(EMAIL_JOB_GROUP));
        for (JobKey jobKey : jobKeySet) {
            JobDetail jobDetail = scheduler.getJobDetail(jobKey);
            List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);

            for (Trigger trigger : triggers) {
                ScheduleResponse job = ScheduleResponse.builder()
                        .jobId(jobKey.getName())
                        .triggerId(trigger.getKey().getName())
                        .triggerTime(ZonedDateTime.ofInstant(trigger.getStartTime().toInstant(), ZoneOffset.UTC))
                        .templateEmailDetailsList((List<TemplateEmailDetails>) jobDetail.getJobDataMap().get(TEMPLATE_EMAIL_DATA_LIST))
                        .build();
                scheduledJobs.add(job);
            }
        }
        return scheduledJobs;
    }

    private Trigger buildJobTrigger(JobDetail jobDetail, ZonedDateTime triggerTime) {
        String triggerId = UUID.randomUUID().toString();
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(triggerId, EMAIL_TRIGGER_GROUP)
                .startAt(Date.from(triggerTime.toInstant()))
                .withSchedule(SimpleScheduleBuilder.simpleSchedule())
                .build();

    }

    private JobDetail buildJobDetail(List<TemplateEmailDetails> templateEmailDetailsList) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(TEMPLATE_EMAIL_DATA_LIST, templateEmailDetailsList);
        String jobId = UUID.randomUUID().toString();

        return JobBuilder.newJob(EmailJob.class)
                .withIdentity(jobId, EMAIL_JOB_GROUP)
                .usingJobData(jobDataMap)
                .storeDurably()
                .build();
    }

}