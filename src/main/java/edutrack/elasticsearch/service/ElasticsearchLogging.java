package edutrack.elasticsearch.service;

public interface ElasticsearchLogging {

	public String logError(String errorId, String message, String stackTrace);
}
