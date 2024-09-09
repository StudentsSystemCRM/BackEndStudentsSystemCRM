package edutrack.pulsar;

import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.Reader;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class PulsarReaderService {
    private final Reader<byte[]> reader;

    public PulsarReaderService(Reader<byte[]> reader) {
        this.reader = reader;
    }

    public void readMessages() throws PulsarClientException {
        while (true) {
            Message<byte[]> message = reader.readNext();
            System.out.println("Message read: " + new String(message.getData()));
        }
    }

    public void closeReader() throws IOException {
        if (reader != null) {
            reader.close();
        }
    }
}
