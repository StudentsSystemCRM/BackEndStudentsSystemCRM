package edutrack.emailService.service.mailgun;

import edutrack.emailService.entity.EmailStatusEntity;
import edutrack.emailService.exception.MailgunBadRequestException;
import edutrack.emailService.repository.EmailStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.sql.Timestamp;
import java.util.Map;

@Service
public class MailgunServiceImpl {

    @Autowired
    EmailStatusRepository emailStatusRepository;

    @Value("${mailgun.signature}")
    String mailgunSignature;

    public void handleMailgunWebhook(Map<String, Object> payload) {
        if (isValidMailgun(payload)) {
            MailgunData mailgunData = extractData(payload);
            EmailStatusEntity mailStatus = new EmailStatusEntity(
                    mailgunData.messageId,
                    mailgunData.status,
                    mailgunData.timestamp,
                    mailgunData.recipient
            );
            emailStatusRepository.save(mailStatus);
        } else {
            throw new MailgunBadRequestException("It might someone try to tamper the Mailgun response!");
        }
    }

    private boolean isValidMailgun(Map<String, Object> payload) {
        try {
            if (payload == null || !payload.containsKey("signature")) {
                throw new MailgunBadRequestException("There is no a payload or a specific field(s) in the Mailgun request");
            }
            Map<String, Object> signatureData = (Map<String, Object>) payload.get("signature");

            if (signatureData == null || !signatureData.containsKey("token") || !signatureData.containsKey("timestamp") || !signatureData.containsKey("signature")) {
                throw new MailgunBadRequestException("There is no a payload or a specific field(s) in the Mailgun request");
            }
            String token = (String) signatureData.get("token");
            String timestamp = (String) signatureData.get("timestamp");
            String signature = (String) signatureData.get("signature");

            String data = timestamp + token;
            SecretKey keySpec = new SecretKeySpec(mailgunSignature.getBytes(), "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(keySpec);

            byte[]  hmac = mac.doFinal(data.getBytes());
            char[] hexChars = Hex.encode(hmac);
            String computedSignature = new String(hexChars);
            return computedSignature.equals(signature);
        }
        catch (Exception e) {
            throw new MailgunBadRequestException("Something went wrong with computing the Mailgun signature: " + e);
        }
    }

    private MailgunData extractData(Map<String, Object> payload) {
        try {
            if (payload == null || !payload.containsKey("event-data")) {
                throw new MailgunBadRequestException("There is no a payload or a specific field(s) in the Mailgun request");
            }
            Map<String, Object> eventData = (Map<String, Object>) payload.get("event-data");
            if (eventData == null || !eventData.containsKey("event") || !eventData.containsKey("timestamp") || !eventData.containsKey("message") || !eventData.containsKey("recipient")) {
                throw new MailgunBadRequestException("There is no a payload or a specific field(s) in the Mailgun request");
            }

            Map<String, Object> message = (Map<String, Object>) eventData.get("message");
            if (message == null || !message.containsKey("headers")) {
                throw new MailgunBadRequestException("There is no a payload or a specific field(s) in the Mailgun request");
            }
  //          ArrayList<String> recipients = (ArrayList<String>) message.get("recipients");
  //          String recipient = recipients.get(0);
            String recipient = (String) eventData.get("recipient");

            Map<String, Object> headers = (Map<String, Object>) message.get("headers");
            if (headers == null || !headers.containsKey("message-id")) {
                throw new MailgunBadRequestException("There is no a payload or a specific field(s) in the Mailgun request");
            }
            String messageId = (String) headers.get("message-id");
            messageId = messageId.replace("<","").replace(">", "");

            String status = (String) eventData.get("event");

            double timestampDouble = (double) eventData.get("timestamp");
            long timestampLong = (long) timestampDouble;
            Timestamp timestamp = new Timestamp(timestampLong * 1000);

            return new MailgunData(messageId, status, timestamp, recipient);

        }
        catch (Exception e) {
            throw new MailgunBadRequestException("There is some problems within the Mailgun request: " + e);
        }
    }

    private record MailgunData(String messageId, String status, Timestamp timestamp, String recipient) { }
}

