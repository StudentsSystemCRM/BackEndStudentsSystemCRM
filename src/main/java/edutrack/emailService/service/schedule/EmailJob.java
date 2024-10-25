package edutrack.emailService.service.schedule;

import edutrack.emailService.dto.TemplateEmailDetails;
import edutrack.emailService.service.email.TemplateEmailServiceImpl;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class EmailJob implements Job {
    @Autowired
    TemplateEmailServiceImpl templateEmailService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        List<TemplateEmailDetails> templateEmailDetailsList = (List<TemplateEmailDetails>) jobDataMap.get(EmailScheduleServiceImpl.TEMPLATE_EMAIL_DATA_LIST);
        try {
            Map<String, String> messageIds = templateEmailService.sendEmails(templateEmailDetailsList);
        } catch (Exception e) {
  //          logger.error("Error occurred while executing email job: ", e);
            throw new JobExecutionException("Error occurred while executing email job: ", e);
        }
    }
}