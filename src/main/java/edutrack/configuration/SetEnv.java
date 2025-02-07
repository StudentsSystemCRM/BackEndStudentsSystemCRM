package edutrack.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;

@Configuration
public class SetEnv {


	@PostConstruct
	public void systemSet() {
		Dotenv dotenv = Dotenv.configure()
			    .directory(System.getProperty("user.dir")) 
			    .load();
		System.out.println("JWT_ACCESS_SECRET from dotenv: " + dotenv.get("JWT_ACCESS_SECRET"));

		System.setProperty("DB_URL", dotenv.get("DB_URL"));
		System.setProperty("DB_USERNAME", dotenv.get("DB_USERNAME"));
		System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
		System.setProperty("MONGO_URI", dotenv.get("MONGO_URI"));
		System.setProperty("JWT_ACCESS_SECRET", dotenv.get("JWT_ACCESS_SECRET"));
		System.setProperty("JWT_REFRESH_SECRET", dotenv.get("JWT_REFRESH_SECRET"));
		System.setProperty("MAILGUN_API_KEY", dotenv.get("MAILGUN_API_KEY"));
		System.setProperty("MAILGUN_DOMAIN", dotenv.get("MAILGUN_DOMAIN"));
		System.setProperty("MAILGUN_API_BASE_URL", dotenv.get("MAILGUN_API_BASE_URL"));
		System.setProperty("MAILGUN_FROM_EMAIL", dotenv.get("MAILGUN_FROM_EMAIL"));
		System.setProperty("MAILGUN_SIGNATURE", dotenv.get("MAILGUN_SIGNATURE"));
		System.setProperty("ELASTICSEARCH_URL", dotenv.get("ELASTICSEARCH_URL"));
		System.setProperty("ELASTICSEARCH_USERNAME", dotenv.get("ELASTICSEARCH_USERNAME"));
		System.setProperty("ELASTICSEARCH_PASSWORD", dotenv.get("ELASTICSEARCH_PASSWORD"));
	}

}
