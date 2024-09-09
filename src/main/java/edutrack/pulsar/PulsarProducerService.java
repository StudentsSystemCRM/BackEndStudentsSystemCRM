package edutrack.pulsar;

import edutrack.modul.user.dto.response.Role;
import edutrack.security.token.JwtTokenCreator;
import lombok.RequiredArgsConstructor;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClientException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PulsarProducerService {
    private final Producer<byte[]> producer;
    private final JwtTokenCreator jwtTokenCreator;

    public void sendMessage(String message) throws PulsarClientException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            String email = authentication.getName();
            Set<Role> roles = authentication.getAuthorities().stream()
                    .map(authority -> Role.valueOf(((SimpleGrantedAuthority) authority).getAuthority().replace("ROLE_", "")))
                    .collect(Collectors.toSet());
            String token = jwtTokenCreator.createToken(email, roles);

            producer.newMessage()
                    .value(message.getBytes())
                    .property("Authorization", "Bearer " + token)
                    .send();
        } else {
            throw new IllegalStateException("Authentication is not available.");
        }
    }

    public void closeProducer() throws PulsarClientException {
        if (producer != null) {
            producer.close();
        }
    }
}
