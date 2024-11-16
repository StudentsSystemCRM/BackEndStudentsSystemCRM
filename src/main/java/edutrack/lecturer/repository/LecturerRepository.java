package edutrack.lecturer.repository;

import edutrack.lecturer.constant.LecturerStatus;
import edutrack.lecturer.entity.LecturerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LecturerRepository extends JpaRepository<LecturerEntity, Long> {
    LecturerEntity findByEmail(String email);
    List<LecturerEntity> findByStatus(LecturerStatus status);
    List<LecturerEntity> findByCity(String city);
    List<LecturerEntity> findByLastName(String lastName);
    boolean existsByEmail(String email);
}
