package edutrack.emailService.service.email;

import edutrack.emailService.dto.EmailDetails;

import java.util.List;
import java.util.Map;

public interface EmailService {
    Map<String, String> sendEmails(List<EmailDetails> emailDetailsList);
}