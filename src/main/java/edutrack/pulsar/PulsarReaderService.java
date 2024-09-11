package edutrack.pulsar;

import edutrack.security.token.JwtRequestFilter;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.Reader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class PulsarReaderService {
    Reader<byte[]> reader;

    private static final Logger logger = LoggerFactory.getLogger(JwtRequestFilter.class);

    public void readMessages() throws PulsarClientException {
        while (true) {
            logger.info("READER LOGGER");

            Message<byte[]> message = reader.readNext();
            logger.info("READER LOGGER. READ MESSAGE {}", message);
        }
    }

    public void closeReader() throws IOException {
        if (reader != null) {
            reader.close();
        }
    }
}
