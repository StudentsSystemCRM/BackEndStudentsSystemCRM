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
                .authentication(AuthenticationFactory.token("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0LXVzZXIifQ.unTlQwtZ7CgiQ_3wLPJVxbyFRaGYsArvGo702m-wLrw"))
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
                .deadLetterPolicy(DeadLetterPolicy.builder()
                        .maxRedeliverCount(3)
                        .build())
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
