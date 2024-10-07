package edutrack.elasticsearch.config;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticsearchConfig {

    private static final String BONSAI_URL = "https://no-search-3715571296.eu-central-1.bonsaisearch.net:443";
    private static final String BONSAI_USERNAME = "qq894pgf6y";
    private static final String BONSAI_PASSWORD = "kwf0hcln1m";

    @Bean
    RestHighLevelClient client() {
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, 
            new UsernamePasswordCredentials(BONSAI_USERNAME, BONSAI_PASSWORD));

        RestClientBuilder builder = RestClient.builder(HttpHost.create(BONSAI_URL))
            .setHttpClientConfigCallback(httpAsyncClientBuilder ->
                httpAsyncClientBuilder.setDefaultCredentialsProvider(credentialsProvider));
        
        return new RestHighLevelClient(builder);
    }
}
