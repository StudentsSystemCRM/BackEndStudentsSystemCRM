package edutrack.elasticsearch.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Log4j2
public class ElasticsearchLoggingImp implements ElasticsearchLogging {

	RestHighLevelClient client;
	static int test=0;
	@Override
	public String saveLog(String message, String stackTrace, String requestUrl, 
			String requestMethod, String username) {
		String errorId = UUID.randomUUID().toString();
		try {
			Map<String, Object> logData = new HashMap<>();
			logData.put("errorId", errorId);
			logData.put("message", message);
			logData.put("stackTrace", stackTrace);
			logData.put("timestamp", Instant.now().toString());
			logData.put("requestUrl", requestUrl);
			logData.put("requestMethod", requestMethod);
			logData.put("username", username);

			ObjectMapper objectMapper = new ObjectMapper();
			String jsonLog = objectMapper.writeValueAsString(logData);
			if(test%2==1)log.error(jsonLog);
			if(test%2==0)log.info(jsonLog);
			test++;
			return errorId;
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Error logging to Elasticsearch: " + e.getMessage());
			return errorId;
		}
	}
	
	@Override
	public String saveLogExeption(Exception ex) {
		StringBuilder result = new StringBuilder();
		for (StackTraceElement element : ex.getStackTrace()) {
			result.append(element.toString()).append("\n");
		}
		String stackTraceAsString = result.toString();
		
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		String requestUrl = request.getRequestURI();
		String requestMethod = request.getMethod();

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = (authentication != null && authentication.isAuthenticated()) ? authentication.getName() : "Anonymous";

		return saveLog(ex.getMessage(), 
				stackTraceAsString,
				requestUrl, 
				requestMethod, 
				username);
	}

	@Override
	@Scheduled(cron = "0 0 0 * * ?")
	public void deleteOldLogs() {

		String oldDate = LocalDate.now().minusDays(2).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

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
