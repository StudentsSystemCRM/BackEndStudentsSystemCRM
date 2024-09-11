package edutrack.pulsar;

import edutrack.security.token.JwtRequestFilter;
import edutrack.security.token.JwtTokenValidator;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.PulsarClientException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class PulsarConsumerService {
    Consumer<byte[]> consumer;
    JwtTokenValidator jwtTokenValidator;

    private static final Logger logger = LoggerFactory.getLogger(JwtRequestFilter.class);

    @PostConstruct
    private void startConsumer() {
        new Thread(() -> {
            try {
                receiveMessages();
            } catch (PulsarClientException e) {
                logger.error("Failed Consumer: ", e);
            }
        }).start();
    }

    public void receiveMessages() throws PulsarClientException {
        while (true) {
            Message<byte[]> msg = consumer.receive();

            String payload = new String(msg.getData());
            logger.info("CONSUMER LOGGER {}", payload);

            // json parse to get message and token
            JSONObject json = new JSONObject(payload);
            String message = json.optString("message");
            String token = json.getString("jwtToken");

            try {
                // Validate token
                if (jwtTokenValidator.validateToken(token)) {
                    logger.info("CONSUMER LOGGER. RECEIVE MESSAGE {}", message);

                    consumer.acknowledge(msg);
                } else {
                    logger.info("CONSUMER LOGGER. Invalid token received.");
                    consumer.negativeAcknowledge(msg);
                }
            } catch (Exception e) {
                consumer.negativeAcknowledge(msg);
            }
        }
    }

    public void closeConsumer() throws PulsarClientException {
        if (consumer != null)
            consumer.close();
    }
}
