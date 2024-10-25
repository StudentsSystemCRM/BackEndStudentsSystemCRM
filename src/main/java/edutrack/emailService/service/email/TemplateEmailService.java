package edutrack.emailService.service.email;

import edutrack.emailService.dto.TemplateEmailDetails;

import java.util.List;
import java.util.Map;

public interface TemplateEmailService  {
    Map<String, String> sendEmails(List<TemplateEmailDetails> templateEmailDetails);
}