package edutrack.modul.reminder.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import edutrack.modul.reminder.entity.StudentReminders;

public interface StudentRemindersRepository extends JpaRepository<StudentReminders, Long> {
	
	List<StudentReminders> findByStudentId (Long id);
}
