package edutrack.schedule.repository;

import edutrack.schedule.constant.SheduleType;
import edutrack.schedule.entity.GroupScheduleEntity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupScheduleRepository extends JpaRepository<GroupScheduleEntity, Long> {
	
	List<GroupScheduleEntity> findByGroupId(Long scheduleId);

	List<GroupScheduleEntity> findAllBySheduleType(SheduleType sheduleType);
}
