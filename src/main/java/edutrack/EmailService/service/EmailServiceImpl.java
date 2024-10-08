package edutrack.EmailService.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edutrack.EmailService.dto.EmailDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmailServiceImpl implements EmailService{

    @Value("${mailgun.api.key}")
    String apiKey;

    @Value("${mailgun.domain}")
    String domain;

    @Value("${mailgun.api.base-url}")
    String baseUrl;

    @Value("${mailgun.from-email}")
    String fromEmail;

    final RestTemplate restTemplate;

    public EmailServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Map<String, String> sendEmails(List<EmailDetails> emailDetailsList) {
        Map<String,String> messageIds = new HashMap<>();
        emailDetailsList.forEach(emailDetails -> {
            String messageId = sendEmail(emailDetails);
            messageIds.put(emailDetails.getRecipient(), messageId);
        });
        return messageIds;
    }

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String sendEmail(EmailDetails emailDetails) {
        String apiUrl = baseUrl + "/" + domain + "/messages";
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("api", apiKey);

        MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("from", fromEmail);
        requestBody.add("to", emailDetails.getRecipient());
        requestBody.add("subject", emailDetails.getSubject());
        requestBody.add("text", emailDetails.getBody());

        if (emailDetails.getBase64Attachments() != null) {
            emailDetails.getBase64Attachments().forEach(base64Attachment -> requestBody.add("attachment", base64Attachment));
        }

        HttpEntity<MultiValueMap> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    new URI(apiUrl),
                    HttpMethod.POST,
                    request,
                    String.class
            );
            return extractMessageIdFromResponse(response.getBody());
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String extractMessageIdFromResponse(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            String messageId = root.path("id").asText();
            return messageId.replace("<","").replace(">","");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
