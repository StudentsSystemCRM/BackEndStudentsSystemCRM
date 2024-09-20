package edutrack.EmailService.service;

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
import java.util.List;

@Service
public class EmailImp implements EmailService{

    @Value("${mailgun.api.key}")
    String apiKey;

    @Value("${mailgun.domain}")
    String domain;

    @Value("${mailgun.api.base-url}")
    String baseUrl;

    @Value("${mailgun.from-email}")
    String fromEmail;

    final RestTemplate restTemplate;

    public EmailImp(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void sendEmails(List<EmailDetails> emailDetailsList) {
        emailDetailsList.forEach(this::sendEmail);
    }

    private void sendEmail(EmailDetails emailDetails) {
        String apiUrl = baseUrl + "/" + domain + "/messages";
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("api", apiKey);

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("from", fromEmail);
        requestBody.add("to", emailDetails.getRecipient());
        requestBody.add("subject", emailDetails.getSubject());
        requestBody.add("text", emailDetails.getBody());

        HttpEntity<MultiValueMap> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    new URI(apiUrl),
                    HttpMethod.POST,
                    request,
                    String.class
            );
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
