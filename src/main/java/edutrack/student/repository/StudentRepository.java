package edutrack.student.repository;
import java.util.List;

import edutrack.student.entity.StudentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<StudentEntity, Long> {
	StudentEntity findByEmail(String email);
	List<StudentEntity> findByFirstName(String name);
	List<StudentEntity> findByLastName(String name);
	List<StudentEntity> findByFirstNameAndLastName(String name, String surname);
}
