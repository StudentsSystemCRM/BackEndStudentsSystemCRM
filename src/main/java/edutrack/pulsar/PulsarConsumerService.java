package edutrack.pulsar;

import lombok.RequiredArgsConstructor;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.PulsarClientException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PulsarConsumerService {
    private final Consumer<byte[]> consumer;

    public void receiveMessages() throws PulsarClientException {
        while (true) {
            Message<byte[]> message = consumer.receive();
            try {
                System.out.println("Message received: " + new String(message.getData()));
                consumer.acknowledge(message);
            } catch (Exception e) {
                consumer.negativeAcknowledge(message);
            }
        }
    }

    public void closeConsumer() throws PulsarClientException {
        if (consumer != null)
            consumer.close();
    }
}
