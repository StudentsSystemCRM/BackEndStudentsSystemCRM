package edutrack.modul.user.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import edutrack.modul.user.entity.Account;

public interface UserRepository extends MongoRepository<Account, String> {
    Account findByEmail(String email);
}
