package edutrack.elasticsearch.service;

public interface ElasticsearchLogging {

	public String saveLog(
			String message, 
			String stackTraceAsString,
			String requestUrl, 
			String requestMethod, 
			String username);
	public String saveLogExeption(Exception ex);
	void deleteOldLogs();
}
