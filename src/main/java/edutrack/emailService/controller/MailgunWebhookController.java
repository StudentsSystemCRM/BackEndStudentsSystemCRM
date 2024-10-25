package edutrack.emailService.controller;

import edutrack.emailService.service.mailgun.MailgunServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/mailgun")
public class MailgunWebhookController {

    private final MailgunServiceImpl mailgunService;

    public MailgunWebhookController(MailgunServiceImpl mailgunService) {
        this.mailgunService = mailgunService;
    }

    @PostMapping("/webhook")
    public ResponseEntity<?> handleMailgunWebhook(@RequestBody Map<String, Object> payload) {
            mailgunService.handleMailgunWebhook(payload);
        return ResponseEntity.ok().build();
    }

}
