package edutrack.repository;

import edutrack.entity.students.StudentReminders;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRemindersRepository extends JpaRepository<StudentReminders, Long> {
}
