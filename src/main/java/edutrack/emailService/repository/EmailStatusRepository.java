package edutrack.emailService.repository;

import edutrack.emailService.entity.EmailStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailStatusRepository extends JpaRepository<EmailStatusEntity, String> {
}
