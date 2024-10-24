package edutrack.emailService.controller;

import edutrack.emailService.exception.MailgunBadRequestException;
import edutrack.emailService.service.MailgunServiceImpl;
import edutrack.exception.handler.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class MailgunWebhookControllerTest {

    private MockMvc mockMvc;

    @Mock
    private MailgunServiceImpl mailgunService;

    private MailgunWebhookController mailgunWebhookController;

    @BeforeEach
    void setUp() {
        mailgunWebhookController = new MailgunWebhookController(mailgunService);

        mockMvc = MockMvcBuilders.standaloneSetup(mailgunWebhookController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void testHandleMailgunWebhook_successful() throws Exception {
        String payloadJson = "{\n" +
                "  \"signature\": {\n" +
                "    \"timestamp\": \"1623789303\",\n" +
                "    \"token\": \"e3f2f3f4f5f6f7f8f9f0\",\n" +
                "    \"signature\": \"abcdef1234567890abcdef1234567890abcdef12\"\n" +
                "  },\n" +
                "  \"event-data\": {\n" +
                "    \"event\": \"delivered\",\n" +
                "    \"timestamp\": 1623789303,\n" +
                "    \"message\": {\n" +
                "      \"headers\": {\n" +
                "        \"message-id\": \"<test-message-id>\"\n" +
                "      }\n" +
                "    },\n" +
                "    \"recipient\": \"recipient@example.com\"\n" +
                "  }\n" +
                "}";

        doNothing().when(mailgunService).handleMailgunWebhook(any(Map.class));

        mockMvc.perform(post("/api/mailgun/webhook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payloadJson))
                .andExpect(status().isOk());

        verify(mailgunService, times(1)).handleMailgunWebhook(any(Map.class));
    }

    @Test
    void testHandleMailgunWebhook_exception() throws Exception {
        String payloadJson = "{\n" +
                "  \"signature\": {\n" +
                "    \"timestamp\": \"1623789303\",\n" +
                "    \"token\": \"e3f2f3f4f5f6f7f8f9f0\",\n" +
                "    \"signature\": \"abcdef1234567890abcdef1234567890abcdef12\"\n" +
                "  },\n" +
                "  \"event-data\": {\n" +
                "    \"event\": \"delivered\",\n" +
                "    \"timestamp\": 1623789303,\n" +
                "    \"message\": {\n" +
                "      \"headers\": {\n" +
                "        \"message-id\": \"<test-message-id>\"\n" +
                "      }\n" +
                "    },\n" +
                "    \"recipient\": \"recipient@example.com\"\n" +
                "  }\n" +
                "}";

        doThrow(new MailgunBadRequestException("Invalid payload"))
                .when(mailgunService).handleMailgunWebhook(any(Map.class));

        mockMvc.perform(post("/api/mailgun/webhook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payloadJson))
                .andExpect(status().isBadRequest());

        verify(mailgunService, times(1)).handleMailgunWebhook(any(Map.class));
    }
}
