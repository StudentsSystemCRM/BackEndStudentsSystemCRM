package edutrack.security.repository;

import edutrack.security.entity.RefreshTokenEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends MongoRepository<RefreshTokenEntity, String> {
    Optional<RefreshTokenEntity> findByToken(String token);

    int deleteByUserEmail(String userId);
}
