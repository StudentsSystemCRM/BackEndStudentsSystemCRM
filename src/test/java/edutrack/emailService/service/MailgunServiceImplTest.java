package edutrack.emailService.service;

import edutrack.emailService.entity.EmailStatusEntity;
import edutrack.emailService.exception.MailgunBadRequestException;
import edutrack.emailService.repository.EmailStatusRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MailgunServiceImplTest {

    @InjectMocks
    private MailgunServiceImpl mailgunService;

    @Mock
    private EmailStatusRepository emailStatusRepository;

    private final String mailgunSignature = "test-mailgun-signature";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(mailgunService, "mailgunSignature", mailgunSignature);
    }

    @Test
    void testHandleMailgunWebhook_validPayload() throws Exception {
        Map<String, Object> payload = createValidPayload();

        mailgunService.handleMailgunWebhook(payload);

        ArgumentCaptor<EmailStatusEntity> captor = ArgumentCaptor.forClass(EmailStatusEntity.class);
        verify(emailStatusRepository, times(1)).save(captor.capture());

        EmailStatusEntity savedEntity = captor.getValue();

        assertNotNull(savedEntity);
        assertEquals("test-message-id", savedEntity.getMessageId());
        assertEquals("delivered", savedEntity.getStatus());
        assertEquals("recipient@example.com", savedEntity.getRecipient());
        assertNotNull(savedEntity.getTimestamp());
    }

    @Test
    void testHandleMailgunWebhook_invalidSignature() throws Exception {
        Map<String, Object> payload = createValidPayload();
        Map<String, Object> signatureData = (Map<String, Object>) payload.get("signature");
        signatureData.put("signature", "invalid-signature");

        MailgunBadRequestException exception = assertThrows(MailgunBadRequestException.class, () -> {
            mailgunService.handleMailgunWebhook(payload);
        });

        assertEquals("It might someone try to tamper the Mailgun response!", exception.getMessage());
        verify(emailStatusRepository, never()).save(any(EmailStatusEntity.class));
    }

    @Test
    void testHandleMailgunWebhook_missingFields() {
        // Prepare a payload missing the 'event-data' field
        Map<String, Object> payload = new HashMap<>();
        payload.put("signature", new HashMap<>());

        MailgunBadRequestException exception = assertThrows(MailgunBadRequestException.class, () -> {
            mailgunService.handleMailgunWebhook(payload);
        });

        assertTrue(exception.getMessage().contains("There is no a payload or a specific field(s) in the Mailgun request"));
        verify(emailStatusRepository, never()).save(any(EmailStatusEntity.class));
    }

    @Test
    void testHandleMailgunWebhook_nullPayload() {
        MailgunBadRequestException exception = assertThrows(MailgunBadRequestException.class, () -> {
            mailgunService.handleMailgunWebhook(null);
        });

        assertTrue(exception.getMessage().contains("There is no a payload or a specific field(s) in the Mailgun request"));
        verify(emailStatusRepository, never()).save(any(EmailStatusEntity.class));
    }

    @Test
    void testIsValidMailgun_invalidSignatureComputation() throws Exception {
        Map<String, Object> payload = createValidPayload();
        ReflectionTestUtils.setField(mailgunService, "mailgunSignature", null);

        MailgunBadRequestException exception = assertThrows(MailgunBadRequestException.class, () -> {
            mailgunService.handleMailgunWebhook(payload);
        });

        assertTrue(exception.getMessage().contains("Something went wrong with computing the Mailgun signature"));
        verify(emailStatusRepository, never()).save(any(EmailStatusEntity.class));
    }

    @Test
    void testExtractData_missingMessageId() throws Exception {
        Map<String, Object> payload = createValidPayload();
        Map<String, Object> eventData = (Map<String, Object>) payload.get("event-data");
        Map<String, Object> message = (Map<String, Object>) eventData.get("message");
        Map<String, Object> headers = (Map<String, Object>) message.get("headers");
        headers.remove("message-id");

        MailgunBadRequestException exception = assertThrows(MailgunBadRequestException.class, () -> {
            mailgunService.handleMailgunWebhook(payload);
        });

        assertTrue(exception.getMessage().contains("There is no a payload or a specific field(s) in the Mailgun request"));
        verify(emailStatusRepository, never()).save(any(EmailStatusEntity.class));
    }

    // Helper method to create a valid payload
    private Map<String, Object> createValidPayload() throws Exception {
        Map<String, Object> payload = new HashMap<>();

        // Signature
        Map<String, Object> signatureData = new HashMap<>();
        String token = "test-token";
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        String data = timestamp + token;
        String computedSignature = computeHmac(data, mailgunSignature);

        signatureData.put("token", token);
        signatureData.put("timestamp", timestamp);
        signatureData.put("signature", computedSignature);
        payload.put("signature", signatureData);

        // Event Data
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("event", "delivered");
        eventData.put("timestamp", Double.valueOf(timestamp));
        eventData.put("recipient", "recipient@example.com");

        Map<String, Object> message = new HashMap<>();
        Map<String, Object> headers = new HashMap<>();
        headers.put("message-id", "<test-message-id>");
        message.put("headers", headers);
        eventData.put("message", message);

        payload.put("event-data", eventData);

        return payload;
    }

    private String computeHmac(String data, String key) throws Exception {
        SecretKey keySpec = new SecretKeySpec(key.getBytes(), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(keySpec);
        byte[] hmac = mac.doFinal(data.getBytes());
        char[] hexChars = org.springframework.security.crypto.codec.Hex.encode(hmac);
        return new String(hexChars);
    }
}
