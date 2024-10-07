package edutrack.elasticsearch.config;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:elastic.properties")
public class ElasticsearchConfig {

	@Value("${elasticsearch.url}")
    private String BONSAI_URL;
	
	@Value("${elasticsearch.username}")
    private String BONSAI_USERNAME;
	
	@Value("${elasticsearch.password}")
    private String BONSAI_PASSWORD;

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
