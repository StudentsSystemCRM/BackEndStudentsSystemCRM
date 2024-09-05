package edutrack.modul.lecturer.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import edutrack.modul.lecturer.entity.Lecturer;

public interface LecturerRepository extends JpaRepository<Lecturer, Long> {

}
