package edutrack.schedule.repository;

import edutrack.schedule.entity.GroupScheduleEntity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GroupScheduleRepository extends JpaRepository<GroupScheduleEntity, Long> {
	
	public static final String GROUP_EXISTS_BY_ID = "SELECT id FROM groups WHERE id = :group_id";

	@Query(value = GROUP_EXISTS_BY_ID, nativeQuery = true)
	public List<Long> groupExistsById(@Param("group_id") Long group_id);
	
	List<GroupScheduleEntity> findByGroupId(Long scheduleId);
}
