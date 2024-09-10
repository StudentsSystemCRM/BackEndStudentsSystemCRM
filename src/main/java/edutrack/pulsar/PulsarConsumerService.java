package edutrack.pulsar;

import edutrack.security.token.JwtTokenValidator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.PulsarClientException;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
public class PulsarConsumerService {
    private final Consumer<byte[]> consumer;
    private final JwtTokenValidator jwtTokenValidator;

    @PostConstruct
    public void startConsumer() {
        new Thread(() -> {
            try {
                receiveMessages();
            } catch (PulsarClientException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void receiveMessages() throws PulsarClientException {
        while (true) {
            Message<byte[]> message = consumer.receive();

            String token = message.getProperty("Authorization").replace("Bearer ", "");

            if (jwtTokenValidator.validateToken(token)) {
                System.out.println("Valid message received: " + new String(message.getData()));
                consumer.acknowledge(message);
            } else {
                System.out.println("Invalid token, message discarded.");
                consumer.negativeAcknowledge(message);
            }
        }
    }

    public void closeConsumer() throws PulsarClientException {
        if (consumer != null)
            consumer.close();
    }
}
