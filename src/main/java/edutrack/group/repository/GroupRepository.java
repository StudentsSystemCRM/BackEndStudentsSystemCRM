package edutrack.group.repository;
import edutrack.group.constant.GroupStatus;
import edutrack.group.entity.GroupEntity;

import java.time.DayOfWeek;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends JpaRepository<GroupEntity, Long> {
	
	public static final String GROUP_STUDENTS_IDS = "SELECT students_id FROM groups_students WHERE group_id=:group_id";
	public static final String GROUP_LESSONS_DAY = "SELECT group_id FROM lessons_days_time WHERE day_of_week=:day_of_week";
	public static final String GROUP_WEBINARS_DAY = "SELECT group_id FROM webinars_days_time WHERE day_of_week=:day_of_week";
	
	GroupEntity findByName(String name);
	List<GroupEntity> findByNameContainingIgnoreCase(String name);
	List<GroupEntity> findByStatus(GroupStatus status);
	
	@Query(value = GROUP_STUDENTS_IDS, nativeQuery = true)
	public List<Long> findStudentsIdsByGroup(@Param("group_id") Long group_id);
	
	@Query(value = GROUP_LESSONS_DAY, nativeQuery = true)
	public List<Long> findGroupsIdsByLessonsDay(@Param("day_of_week") DayOfWeek day_of_week);
	
	@Query(value = GROUP_WEBINARS_DAY, nativeQuery = true)
	public List<Long> findGroupsIdsByWebinarsDay(@Param("day_of_week") DayOfWeek day_of_week);
}
