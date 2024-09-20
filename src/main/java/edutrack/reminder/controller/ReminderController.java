package edutrack.reminder.controller;

import edutrack.EmailService.dto.EmailDetails;
import edutrack.EmailService.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reminder")
public class ReminderController {
    private final EmailService emailService;

    @Autowired
    public ReminderController(EmailService emailService){
        this.emailService = emailService;
    }

    @PostMapping("/send-emails")
    public String sendEmails(@RequestBody List<EmailDetails> emailDetailsList){
        emailService.sendEmails(emailDetailsList);
        return "email sent";
    }
}
