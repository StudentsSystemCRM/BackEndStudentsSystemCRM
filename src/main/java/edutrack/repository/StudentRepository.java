package edutrack.repository;
import edutrack.entity.students.Student;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {

	Student findByEmail(String email);
	List<Student> findByFirstName(String name);
	List<Student> findByLastName(String name);
	List<Student> findByFirstNameAndLastName(String name, String surname);
}
