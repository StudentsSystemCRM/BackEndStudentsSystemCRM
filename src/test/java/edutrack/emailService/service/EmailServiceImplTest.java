package edutrack.emailService.service;

import edutrack.emailService.dto.EmailDetails;
import edutrack.emailService.service.email.EmailServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EmailServiceImplTest {

    @InjectMocks
    private EmailServiceImpl emailService;

    @Mock
    private RestTemplate restTemplate;

    @Captor
    private ArgumentCaptor<HttpEntity<MultiValueMap<String, Object>>> httpEntityCaptor;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(emailService, "apiKey", "test-api-key");
        ReflectionTestUtils.setField(emailService, "domain", "test-domain");
        ReflectionTestUtils.setField(emailService, "baseUrl", "https://api.mailgun.net/v3");
        ReflectionTestUtils.setField(emailService, "fromEmail", "test@example.com");
    }

    @Test
    public void testSendEmails_successful() throws Exception {
        EmailDetails email1 = new EmailDetails(
                "recipient1@example.com",
                "Subject 1",
                "Body 1",
                null
        );

        EmailDetails email2 = new EmailDetails(
                "recipient2@example.com",
                "Subject 2",
                "Body 2",
                null
        );

        List<EmailDetails> emailDetailsList = Arrays.asList(email1, email2);

        String messageId1 = "message-id-12345";
        String responseBody1 = "{\"id\":\"<" + messageId1 + ">\",\"message\":\"Queued. Thank you.\"}";
        ResponseEntity<String> responseEntity1 = new ResponseEntity<>(responseBody1, HttpStatus.OK);

        String messageId2 = "message-id-67890";
        String responseBody2 = "{\"id\":\"<" + messageId2 + ">\",\"message\":\"Queued. Thank you.\"}";
        ResponseEntity<String> responseEntity2 = new ResponseEntity<>(responseBody2, HttpStatus.OK);

        when(restTemplate.exchange(
                any(URI.class),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(responseEntity1, responseEntity2);

        Map<String, String> messageIds = emailService.sendEmails(emailDetailsList);

        verify(restTemplate, times(2)).exchange(
                any(URI.class),
                eq(HttpMethod.POST),
                httpEntityCaptor.capture(),
                eq(String.class)
        );

        assertEquals(2, messageIds.size());
        assertEquals(messageId1, messageIds.get("recipient1@example.com"));
        assertEquals(messageId2, messageIds.get("recipient2@example.com"));

        List<HttpEntity<MultiValueMap<String, Object>>> allRequests = httpEntityCaptor.getAllValues();
        assertEquals(2, allRequests.size());

        HttpEntity<MultiValueMap<String, Object>> request1 = allRequests.get(0);
        MultiValueMap<String, Object> body1 = request1.getBody();
        assertEquals("test@example.com", body1.getFirst("from"));
        assertEquals("recipient1@example.com", body1.getFirst("to"));
        assertEquals("Subject 1", body1.getFirst("subject"));
        assertEquals("Body 1", body1.getFirst("text"));

        HttpEntity<MultiValueMap<String, Object>> request2 = allRequests.get(1);
        MultiValueMap<String, Object> body2 = request2.getBody();
        assertEquals("test@example.com", body2.getFirst("from"));
        assertEquals("recipient2@example.com", body2.getFirst("to"));
        assertEquals("Subject 2", body2.getFirst("subject"));
        assertEquals("Body 2", body2.getFirst("text"));
    }

    @Test
    public void testSendEmails_withAttachments() throws Exception {
        List<String> attachments = Arrays.asList("base64-attachment-1", "base64-attachment-2");
        EmailDetails email = new EmailDetails(
                "recipient@example.com",
                "Subject with Attachment",
                "Body with Attachment",
                attachments
        );

        List<EmailDetails> emailDetailsList = Collections.singletonList(email);

        String messageId = "message-id-attachment";
        String responseBody = "{\"id\":\"<" + messageId + ">\",\"message\":\"Queued. Thank you.\"}";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);

        when(restTemplate.exchange(
                any(URI.class),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(responseEntity);

        Map<String, String> messageIds = emailService.sendEmails(emailDetailsList);

        verify(restTemplate, times(1)).exchange(
                any(URI.class),
                eq(HttpMethod.POST),
                httpEntityCaptor.capture(),
                eq(String.class)
        );

        assertEquals(1, messageIds.size());
        assertEquals(messageId, messageIds.get("recipient@example.com"));

        HttpEntity<MultiValueMap<String, Object>> request = httpEntityCaptor.getValue();
        MultiValueMap<String, Object> body = request.getBody();
        List<Object> attachmentValues = body.get("attachment");
        assertNotNull(attachmentValues);
        assertEquals(2, attachmentValues.size());
        assertTrue(attachmentValues.contains("base64-attachment-1"));
        assertTrue(attachmentValues.contains("base64-attachment-2"));
    }

    @Test
    public void testSendEmails_withErrorResponse() throws Exception {
        // Prepare test data
        EmailDetails email = new EmailDetails(
                "recipient@example.com",
                "Test Subject",
                "Test Body",
                null
        );

        List<EmailDetails> emailDetailsList = Collections.singletonList(email);

        String responseBody = "{\"message\":\"'to' parameter is missing\"}";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);

        when(restTemplate.exchange(
                any(URI.class),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(responseEntity);

        Map<String, String> messageIds = emailService.sendEmails(emailDetailsList);

        assertEquals(1, messageIds.size());
        assertNull(messageIds.get("recipient@example.com"));
    }

    @Test
    public void testSendEmails_withException() throws Exception {
        EmailDetails email = new EmailDetails(
                "recipient@example.com",
                "Test Subject",
                "Test Body",
                null
        );

        List<EmailDetails> emailDetailsList = Collections.singletonList(email);

        when(restTemplate.exchange(
                any(URI.class),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class)
        )).thenThrow(new RuntimeException("RestTemplate exception"));

        Map<String, String> messageIds = emailService.sendEmails(emailDetailsList);

        assertEquals(1, messageIds.size());
        assertNull(messageIds.get("recipient@example.com"));
    }
}
