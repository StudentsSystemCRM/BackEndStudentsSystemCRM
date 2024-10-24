package edutrack.emailService.service;

import edutrack.emailService.dto.TemplateEmailDetails;
import edutrack.emailService.dto.request.RescheduleRequest;
import edutrack.emailService.dto.request.ScheduleRequest;
import edutrack.emailService.dto.response.ScheduleResponse;
import edutrack.emailService.exception.TriggerNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;

import java.time.ZonedDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmailScheduleServiceImplTest {

    @InjectMocks
    private EmailScheduleServiceImpl emailScheduleService;

    @Mock
    private Scheduler scheduler;

    @Captor
    private ArgumentCaptor<JobDetail> jobDetailCaptor;

    @Captor
    private ArgumentCaptor<Trigger> triggerCaptor;

    private static final String TEMPLATE_EMAIL_DATA_LIST = "templateEmailDetailsList";
    private static final String EMAIL_JOB_GROUP = "emailJobs";
    private static final String EMAIL_TRIGGER_GROUP = "emailTriggers";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testScheduleEmailJob_successful() throws Exception {
        List<TemplateEmailDetails> emailDetailsList = Arrays.asList(
                new TemplateEmailDetails(
                        "recipient@example.com",
                        "Test Subject",
                        Arrays.asList("base64Attachment1", "base64Attachment2"),
                        "welcomeTemplate",
                        Map.of("name", "John Doe", "date", "2024-01-01")
                )
        );
        ZonedDateTime triggerTime = ZonedDateTime.now().plusMinutes(5);
        ScheduleRequest scheduleRequest = new ScheduleRequest(triggerTime, emailDetailsList);

        when(scheduler.scheduleJob(any(JobDetail.class), any(Trigger.class))).thenReturn(new Date());

        ScheduleResponse response = emailScheduleService.scheduleEmailJob(scheduleRequest);

        verify(scheduler, times(1)).scheduleJob(jobDetailCaptor.capture(), triggerCaptor.capture());

        JobDetail capturedJobDetail = jobDetailCaptor.getValue();
        Trigger capturedTrigger = triggerCaptor.getValue();

        // Assert job detail
        assertNotNull(capturedJobDetail);
        assertEquals(EMAIL_JOB_GROUP, capturedJobDetail.getKey().getGroup());
        assertEquals(EmailJob.class, capturedJobDetail.getJobClass());
        assertEquals(emailDetailsList, capturedJobDetail.getJobDataMap().get(TEMPLATE_EMAIL_DATA_LIST));

        // Assert trigger
        assertNotNull(capturedTrigger);
        assertEquals(EMAIL_TRIGGER_GROUP, capturedTrigger.getKey().getGroup());
        assertEquals(Date.from(triggerTime.toInstant()), capturedTrigger.getStartTime());

        // Assert response
        assertNotNull(response);
        assertEquals(capturedJobDetail.getKey().getName(), response.getJobId());
        assertEquals(capturedTrigger.getKey().getName(), response.getTriggerId());
        assertEquals(triggerTime, response.getTriggerTime());
        assertEquals(emailDetailsList, response.getTemplateEmailDetailsList());
    }


    @Test
    void testScheduleEmailJob_schedulerException() throws Exception {
        List<TemplateEmailDetails> emailDetailsList = Arrays.asList(
                new TemplateEmailDetails(
                        "recipient@example.com",
                        "Test Subject",
                        null,
                        "welcomeTemplate",
                        Map.of("name", "John Doe", "date", "2024-01-01")
                )
        );
        ZonedDateTime triggerTime = ZonedDateTime.now().plusMinutes(5);
        ScheduleRequest scheduleRequest = new ScheduleRequest(triggerTime, emailDetailsList);

        doThrow(new SchedulerException("Scheduler exception")).when(scheduler).scheduleJob(any(JobDetail.class), any(Trigger.class));

        ScheduleResponse response = emailScheduleService.scheduleEmailJob(scheduleRequest);

        verify(scheduler, times(1)).scheduleJob(any(JobDetail.class), any(Trigger.class));

        assertNull(response);
    }

    @Test
    void testRescheduleEmailJob_successful() throws Exception {
        String triggerId = UUID.randomUUID().toString();
        String jobId = UUID.randomUUID().toString();
        ZonedDateTime newTriggerTime = ZonedDateTime.now().plusMinutes(10);
        RescheduleRequest rescheduleRequest = new RescheduleRequest(jobId, triggerId, newTriggerTime);

        TriggerKey triggerKey = new TriggerKey(triggerId, EMAIL_TRIGGER_GROUP);
        JobKey jobKey = new JobKey(jobId, EMAIL_JOB_GROUP);
        Trigger oldTrigger = TriggerBuilder.newTrigger()
                .withIdentity(triggerKey)
                .forJob(jobKey)
                .build();

        when(scheduler.getTrigger(triggerKey)).thenReturn(oldTrigger);
        when(scheduler.rescheduleJob(eq(triggerKey), any(Trigger.class))).thenReturn(Date.from(newTriggerTime.toInstant()));

        boolean result = emailScheduleService.rescheduleEmailJob(rescheduleRequest);

        verify(scheduler, times(1)).getTrigger(triggerKey);
        verify(scheduler, times(1)).rescheduleJob(eq(triggerKey), triggerCaptor.capture());

        Trigger newTrigger = triggerCaptor.getValue();

        assertTrue(result);
        assertEquals(Date.from(newTriggerTime.toInstant()), newTrigger.getStartTime());
        assertEquals(jobKey, newTrigger.getJobKey());
        assertEquals(triggerKey, newTrigger.getKey());
    }

    @Test
    void testRescheduleEmailJob_triggerNotFound() throws Exception {
        String triggerId = UUID.randomUUID().toString();
        String jobId = UUID.randomUUID().toString();
        ZonedDateTime newTriggerTime = ZonedDateTime.now().plusMinutes(10);
        RescheduleRequest rescheduleRequest = new RescheduleRequest(jobId, triggerId, newTriggerTime);

        TriggerKey triggerKey = new TriggerKey(triggerId, EMAIL_TRIGGER_GROUP);
        when(scheduler.getTrigger(triggerKey)).thenReturn(null);

        assertThrows(TriggerNotFoundException.class, () -> {
            emailScheduleService.rescheduleEmailJob(rescheduleRequest);
        });

        verify(scheduler, times(1)).getTrigger(triggerKey);
        verify(scheduler, times(0)).rescheduleJob(any(TriggerKey.class), any(Trigger.class));
    }

    @Test
    void testCancelEmailJob_successful() throws Exception {
        // Prepare test data
        String jobId = UUID.randomUUID().toString();
        JobKey jobKey = new JobKey(jobId, EMAIL_JOB_GROUP);

        when(scheduler.deleteJob(jobKey)).thenReturn(true);

        boolean result = emailScheduleService.cancelEmailJob(jobId);

        verify(scheduler, times(1)).deleteJob(jobKey);

        assertTrue(result);
    }

    @Test
    void testCancelEmailJob_failure() throws Exception {
        String jobId = UUID.randomUUID().toString();
        JobKey jobKey = new JobKey(jobId, EMAIL_JOB_GROUP);

        when(scheduler.deleteJob(jobKey)).thenReturn(false);

        boolean result = emailScheduleService.cancelEmailJob(jobId);

        verify(scheduler, times(1)).deleteJob(jobKey);

        assertFalse(result);
    }

    @Test
    void testGetAllScheduledEmailJobs() throws Exception {
        String jobId = UUID.randomUUID().toString();
        String triggerId = UUID.randomUUID().toString();
        ZonedDateTime triggerTime = ZonedDateTime.now().plusMinutes(5);

        List<TemplateEmailDetails> emailDetailsList = Arrays.asList(
                new TemplateEmailDetails(
                        "recipient@example.com",
                        "Test Subject",
                        Arrays.asList("base64Attachment1"),
                        "welcomeTemplate",
                        Map.of("name", "John Doe")
                )
        );

        JobKey jobKey = new JobKey(jobId, EMAIL_JOB_GROUP);
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(TEMPLATE_EMAIL_DATA_LIST, emailDetailsList);

        JobDetail jobDetail = JobBuilder.newJob(EmailJob.class)
                .withIdentity(jobKey)
                .usingJobData(jobDataMap)
                .build();

        TriggerKey triggerKey = new TriggerKey(triggerId, EMAIL_TRIGGER_GROUP);
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(triggerKey)
                .forJob(jobKey)
                .startAt(Date.from(triggerTime.toInstant()))
                .build();

        Set<JobKey> jobKeys = new HashSet<>(Arrays.asList(jobKey));
        when(scheduler.getJobKeys(GroupMatcher.jobGroupEquals(EMAIL_JOB_GROUP))).thenReturn(jobKeys);
        when(scheduler.getJobDetail(jobKey)).thenReturn(jobDetail);

        List<Trigger> triggers = Collections.singletonList(trigger);
        when(scheduler.getTriggersOfJob(jobKey)).thenAnswer(invocation -> triggers);

        List<ScheduleResponse> scheduledJobs = emailScheduleService.getAllScheduledEmailJobs();

        verify(scheduler, times(1)).getJobKeys(GroupMatcher.jobGroupEquals(EMAIL_JOB_GROUP));
        verify(scheduler, times(1)).getJobDetail(jobKey);
        verify(scheduler, times(1)).getTriggersOfJob(jobKey);

        assertNotNull(scheduledJobs);
        assertEquals(1, scheduledJobs.size());

        ScheduleResponse response = scheduledJobs.get(0);
        assertEquals(jobId, response.getJobId());
        assertEquals(triggerId, response.getTriggerId());
        assertEquals(triggerTime.toInstant().getEpochSecond(), response.getTriggerTime().toInstant().getEpochSecond());
        assertEquals(emailDetailsList, response.getTemplateEmailDetailsList());
    }



}
