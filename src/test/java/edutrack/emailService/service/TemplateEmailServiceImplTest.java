package edutrack.emailService.service;

import edutrack.emailService.dto.EmailDetails;
import edutrack.emailService.dto.TemplateEmailDetails;
import edutrack.emailService.service.email.EmailService;
import edutrack.emailService.service.email.TemplateEmailServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

class TemplateEmailServiceImplTest {

    @Mock
    private EmailService emailService;

    @InjectMocks
    private TemplateEmailServiceImpl templateEmailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSendEmails() {
        TemplateEmailDetails templateEmailDetails1 = new TemplateEmailDetails(
                "recipient1@example.com", "Subject1", new ArrayList<>(),
                "Hello {{name}}, your order is {{order}}.", Map.of("name", "Alice", "order", "12345")
        );
        TemplateEmailDetails templateEmailDetails2 = new TemplateEmailDetails(
                "recipient2@example.com", "Subject2", new ArrayList<>(),
                "Hello {{name}}, your order is {{order}}.", Map.of("name", "Bob", "order", "67890")
        );

        List<TemplateEmailDetails> templateEmailDetailsList = Arrays.asList(templateEmailDetails1, templateEmailDetails2);

        Map<String, String> expectedResponse = Map.of("recipient1@example.com", "Success", "recipient2@example.com", "Success");
        when(emailService.sendEmails(anyList())).thenReturn(expectedResponse);

        Map<String, String> result = templateEmailService.sendEmails(templateEmailDetailsList);

        verify(emailService, times(1)).sendEmails(anyList());

        assertEquals(expectedResponse, result);
    }

    @Test
    void testConvertToEmailDetails() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        TemplateEmailDetails templateEmailDetails = new TemplateEmailDetails(
                "recipient1@example.com", "Test Subject", new ArrayList<>(),
                "Hello {{name}}!", Map.of("name", "Alice")
        );

        Method method = TemplateEmailServiceImpl.class.getDeclaredMethod("convertToEmailDetails", TemplateEmailDetails.class);
        method.setAccessible(true);

        EmailDetails emailDetails = (EmailDetails) method.invoke(templateEmailService, templateEmailDetails);

        assertEquals("recipient1@example.com", emailDetails.getRecipient());
        assertEquals("Test Subject", emailDetails.getSubject());
        assertEquals("Hello Alice!", emailDetails.getBody());
        assertEquals(templateEmailDetails.getBase64Attachments(), emailDetails.getBase64Attachments());
    }

    @Test
    void testFillTemplate() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String template = "Hello {{name}}, your order {{order}} is confirmed.";
        Map<String, String> placeholders = Map.of("name", "Alice", "order", "12345");

        Method method = TemplateEmailServiceImpl.class.getDeclaredMethod("fillTemplate", String.class, Map.class);
        method.setAccessible(true);
        String result = (String) method.invoke(templateEmailService, template, placeholders);

        assertEquals("Hello Alice, your order 12345 is confirmed.", result);
    }
}

