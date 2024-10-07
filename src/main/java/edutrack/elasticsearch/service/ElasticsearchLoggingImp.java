package edutrack.elasticsearch.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ElasticsearchLoggingImp implements ElasticsearchLogging {

	RestHighLevelClient client;

	@Override
	public String logError(String errorId, String message, String stackTrace, String requestUrl, 
			String requestMethod, String username) {
		try {
			Map<String, Object> logData = new HashMap<>();
			logData.put("errorId", errorId);
			logData.put("message", message);
			logData.put("stackTrace", stackTrace);
			logData.put("timestamp", Instant.now().toString());
			logData.put("requestUrl", requestUrl);
			logData.put("requestMethod", requestMethod);
			logData.put("username", username);

			IndexRequest request = new IndexRequest("app-logs").id(errorId).source(logData, XContentType.JSON);

			IndexResponse response = client.index(request, RequestOptions.DEFAULT);
			return response.getId();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Error logging to Elasticsearch: " + e.getMessage());
			return null;
		}
	}

	@Override
	@Scheduled(cron = "0 0 0 * * ?")
	public void deleteOldLogs() {

		String oldDate = LocalDate.now().minusDays(4).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

		String indexName = "app-logs-" + oldDate;
		DeleteIndexRequest request = new DeleteIndexRequest(indexName);
		try {
			client.indices().delete(request, RequestOptions.DEFAULT);
			System.out.println("Deleted index: " + indexName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
