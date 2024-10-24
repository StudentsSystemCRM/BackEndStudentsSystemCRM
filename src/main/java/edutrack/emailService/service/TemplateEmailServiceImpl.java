package edutrack.emailService.service;

import edutrack.emailService.dto.EmailDetails;
import edutrack.emailService.dto.TemplateEmailDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class TemplateEmailServiceImpl implements TemplateEmailService{
    final
    EmailService emailService;

    public TemplateEmailServiceImpl(EmailService emailService) {
        this.emailService = emailService;
    }

    @Override
    public Map<String, String> sendEmails(List<TemplateEmailDetails> templateEmailDetailsList) {
        List<EmailDetails> emailDetailsList = new ArrayList<>();
        templateEmailDetailsList.forEach(templateEmailDetails -> {
            EmailDetails emailDetails = convertToEmailDetails(templateEmailDetails);
            emailDetailsList.add(emailDetails);
        });
        return emailService.sendEmails(emailDetailsList);
    }

    private EmailDetails convertToEmailDetails(TemplateEmailDetails templateEmailDetails) {
        String filledTemplate = fillTemplate(templateEmailDetails.getTemplate(),templateEmailDetails.getPlaceholders());
        EmailDetails emailDetails = new EmailDetails();
        emailDetails.setRecipient(templateEmailDetails.getRecipient());
        emailDetails.setSubject(templateEmailDetails.getSubject());
        emailDetails.setBase64Attachments(templateEmailDetails.getBase64Attachments());
        emailDetails.setBody(filledTemplate);

        return emailDetails;
    }

    private String fillTemplate(String template, Map<String, String> placeholders) {
        for (Map.Entry<String,String> entry : placeholders.entrySet()) {
            template = template.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }
        return template;
    }
}