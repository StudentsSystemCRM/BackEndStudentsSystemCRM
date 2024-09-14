package edutrack.user.repository;

import edutrack.user.entity.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AccountRepository extends MongoRepository<UserEntity, String> {
    UserEntity findByEmail(String email);
}
