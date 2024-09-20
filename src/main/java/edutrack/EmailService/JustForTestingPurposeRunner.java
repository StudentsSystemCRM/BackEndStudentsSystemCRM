package edutrack.EmailService;

import edutrack.EmailService.dto.EmailDetails;
import edutrack.EmailService.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class JustForTestingPurposeRunner implements CommandLineRunner {
    @Autowired
    EmailService emailService;

    @Override
    public void run(String... args) throws Exception {
//        EmailDetails emailDetails = new EmailDetails(
//                "443rus@gmail.com", "test letter", "Test list2"
//        );
//        EmailDetails emailDetails1 = new EmailDetails(
//                "443rus@gmail.com", "test next letter", "Test list2"
//        );
//        List<EmailDetails> emailDetailsList = new ArrayList<>();
//        emailDetailsList.add(emailDetails);
//        emailDetailsList.add(emailDetails1);
//        emailService.sendEmails(emailDetailsList);
    }
}
