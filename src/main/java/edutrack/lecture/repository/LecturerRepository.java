package edutrack.lecture.repository;
import edutrack.lecture.entity.LecturerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LecturerRepository extends JpaRepository<LecturerEntity, Long> {

}
