package edutrack.configuration;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ElasticsearchDeleteDataScheduler {

    private final RestTemplate restTemplate;

    @Value("${elasticsearch.url}")
    private String elasticsearchUrl;

    @Value("${elasticsearch.username}")
    private String username;

    @Value("${elasticsearch.password}")
    private String password;

    @Scheduled(cron = "0 0 0 * * ?") 
    public void deleteOldLogs() {
        String[] indices = {"logs", "metrics", "traces"};
        String oldDate = LocalDate.now().minusDays(1).format(DateTimeFormatter.ISO_DATE); // Дата за один день назад

        for (String indexName : indices) {
            String deleteUrl = elasticsearchUrl + "/" + indexName + "/_delete_by_query";

            String query = "{ \"query\": { \"range\": { \"@timestamp\": { \"lt\": \"" + oldDate + "\" } } } }";

            HttpHeaders headers = new HttpHeaders();
            headers.setBasicAuth(username, password);
            headers.set("Content-Type", "application/json");

            HttpEntity<String> request = new HttpEntity<>(query, headers);

            try {
                ResponseEntity<String> response = restTemplate.exchange(deleteUrl, HttpMethod.POST, request, String.class);
                if (response.getStatusCode() == HttpStatus.OK) {
                    System.out.println("Deleted data older than " + oldDate + " from index: " + indexName);
                } else {
                	
                    System.err.println("Failed to delete data from index: " + indexName + ". Response code: " + response.getStatusCode());
                }
            } catch (Exception e) {
            	String message = "Exception occurred while deleting data from index: " + indexName;
                System.err.println(message);
                log.error(message);
                e.printStackTrace();
            }
        }
    }
}

