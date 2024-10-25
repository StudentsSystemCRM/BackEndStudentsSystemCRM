package edutrack.emailService;

import edutrack.emailService.service.schedule.EmailSchedulerService;
import edutrack.emailService.service.email.TemplateEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class JustForTestingPurposeRunner implements CommandLineRunner {
    @Autowired
    TemplateEmailService emailService;

    @Autowired
    EmailSchedulerService emailSchedulerService;

    @Override
    public void run(String... args) throws Exception {
//        EmailDetails emailDetails = new EmailDetails(
//                "443rus@gmail.com", "test letter", "Test list2", null
//        );
//        EmailDetails emailDetails1 = new EmailDetails(
//                "443rus@gmail.com", "test next letter", "Test list2", null
//        );
//
//        Map<String, String> placeholders = new HashMap<>();
//        placeholders.put("name", "Vasiya");
//        placeholders.put("team", "Edu");
//        TemplateEmailDetails templateEmailDetails = new TemplateEmailDetails(
//                "443rus@gmail.com", "test letter", null, "Dear customer {{name}} You've got a present {{team}} good luck",
//                placeholders
//        );
//
//        List<EmailDetails> emailDetailsList = new ArrayList<>();
//        emailDetailsList.add(emailDetails);
//        emailDetailsList.add(emailDetails1);
//
//        List<TemplateEmailDetails> templateEmailDetailsList = new ArrayList<>();
//        templateEmailDetailsList.add(templateEmailDetails);
//
//
//               emailService.sendEmails(templateEmailDetailsList);
//
//
//        ZonedDateTime zonedDateTime = ZonedDateTime.of(LocalDateTime.of(2024, 10, 5, 19, 40, 00), ZoneId.of("Asia/Jerusalem"));
//        ScheduleRequest scheduleRequest = new ScheduleRequest(zonedDateTime, templateEmailDetailsList);
//
//
//        emailSchedulerService.scheduleEmailJob(scheduleRequest);
    }
}