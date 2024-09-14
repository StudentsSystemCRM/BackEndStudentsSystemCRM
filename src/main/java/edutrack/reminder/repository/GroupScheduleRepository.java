package edutrack.reminder.repository;

import edutrack.reminder.entity.GroupScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupScheduleRepository extends JpaRepository<GroupScheduleEntity, Long> {
}
