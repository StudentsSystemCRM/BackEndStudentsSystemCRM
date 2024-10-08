package edutrack.EmailService.service;

import edutrack.EmailService.dto.EmailDetails;

import java.util.List;
import java.util.Map;

public interface EmailService {
    Map<String, String> sendEmails(List<EmailDetails> emailDetailsList);
}
