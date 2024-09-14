package edutrack.schedule.repository;
import edutrack.schedule.entity.LecturerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LecturerRepository extends JpaRepository<LecturerEntity, Long> {

}
