package edutrack.modul.student.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edutrack.modul.student.entity.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
	Student findByEmail(String email);
	List<Student> findByFirstName(String name);
	List<Student> findByLastName(String name);
	List<Student> findByFirstNameAndLastName(String name, String surname);
}
