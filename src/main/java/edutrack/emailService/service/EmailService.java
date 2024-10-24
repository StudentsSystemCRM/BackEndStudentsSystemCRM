package edutrack.emailService.service;

import edutrack.emailService.dto.EmailDetails;

import java.util.List;
import java.util.Map;

public interface EmailService {
    Map<String, String> sendEmails(List<EmailDetails> emailDetailsList);
}