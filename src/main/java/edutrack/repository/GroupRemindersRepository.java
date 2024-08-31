package edutrack.repository;

import edutrack.entity.students.GroupReminders;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRemindersRepository extends JpaRepository<GroupReminders, Long> {
}
