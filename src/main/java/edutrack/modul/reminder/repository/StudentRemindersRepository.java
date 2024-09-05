package edutrack.modul.reminder.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import edutrack.modul.reminder.entity.StudentReminders;

public interface StudentRemindersRepository extends JpaRepository<StudentReminders, Long> {
}
