package edutrack.pulsar;

import edutrack.security.token.JwtRequestFilter;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class PulsarProducerService {
    Producer<byte[]> producer;

    private static final Logger logger = LoggerFactory.getLogger(JwtRequestFilter.class);

    public void sendMessage(String message, String jwtToken) throws PulsarClientException {
        // create json obj with message and token
        String payload = String.format("{\"message\":\"%s\",\"jwtToken\":\"%s\"}", message, jwtToken);
        logger.info("PRODUCER LOGGER {}", payload);

        producer.newMessage()
                .value(payload.getBytes())
                .send();
    }

    public void closeProducer() throws PulsarClientException {
        if (producer != null) {
            producer.close();
        }
    }
}
