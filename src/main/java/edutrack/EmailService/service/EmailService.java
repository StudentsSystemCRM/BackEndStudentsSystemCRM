package edutrack.EmailService.service;

import edutrack.EmailService.dto.EmailDetails;

import java.util.List;

public interface EmailService {
    void sendEmails(List<EmailDetails> emailDetailsList);
}
