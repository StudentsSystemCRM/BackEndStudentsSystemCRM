package edutrack.schedule.repository;

import edutrack.schedule.entity.GroupScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupScheduleRepository extends JpaRepository<GroupScheduleEntity, Long> {
}
