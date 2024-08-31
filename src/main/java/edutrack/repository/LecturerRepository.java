package edutrack.repository;
import edutrack.entity.students.Lecturer;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LecturerRepository extends JpaRepository<Lecturer, Long> {

}
