package edutrack.security.entity;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Document(collection = "refresh_tokens")
public class RefreshTokenEntity {
    @Id
    private String id;
    private String userEmail; // connect with UserEntity by user Id
    private String token;
    private Instant expiryDate;
}
