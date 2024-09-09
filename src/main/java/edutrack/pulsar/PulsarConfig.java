package edutrack.pulsar;

import org.apache.pulsar.client.api.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PulsarConfig {
    @Bean
    public PulsarClient pulsarClient() throws PulsarClientException {
        return PulsarClient.builder()
                .serviceUrl("pulsar://localhost:6650")
                .build();
    }

    @Bean
    public Producer<byte[]> producer(PulsarClient client) throws PulsarClientException {
        return client.newProducer()
                .topic("my-topic")
                .create();
    }

    @Bean
    public Consumer<byte[]> consumer(PulsarClient client) throws PulsarClientException {
        return client.newConsumer()
                .topic("my-topic")
                .subscriptionName("my-subscription")
                .subscribe();
    }

    @Bean
    public Reader<byte[]> reader(PulsarClient client) throws PulsarClientException {
        return client.newReader()
                .topic("my-topic")
                .startMessageId(MessageId.earliest)
                .create();
    }
}
