package edutrack.modul.reminder.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import edutrack.modul.reminder.entity.GroupReminders;

public interface GroupRemindersRepository extends JpaRepository<GroupReminders, Long> {
}
