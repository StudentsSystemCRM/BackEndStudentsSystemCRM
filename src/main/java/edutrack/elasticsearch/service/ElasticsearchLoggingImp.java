package edutrack.elasticsearch.service;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ElasticsearchLoggingImp implements ElasticsearchLogging {

	RestHighLevelClient client;

	@Override
	public String logError(String errorId, String message, String stackTrace) {
		try {
			Map<String, Object> logData = new HashMap<>();
			logData.put("errorId", errorId);
			logData.put("message", message);
			logData.put("stackTrace", stackTrace);
			logData.put("timestamp", Instant.now().toString()); 

			IndexRequest request = new IndexRequest("app-logs") 
					.id(errorId) 
					.source(logData, XContentType.JSON);

			IndexResponse response = client.index(request, RequestOptions.DEFAULT);
			return response.getId(); 
		} catch (Exception e) {
			System.err.println("Error logging to Elasticsearch: " + e.getMessage());
			return null;
		}
	}
}
