package edutrack.group.repository;
import edutrack.group.constant.GroupStatus;
import edutrack.group.constant.WeekDay;
import edutrack.group.entity.GroupEntity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends JpaRepository<GroupEntity, Long> {

	public static final String UPDATE_STUDENT_GROUP = "UPDATE groups_students SET group_id=:id where students_id = :student_id and group_id=:old_id";
	public static final String GROUP_LESSONS_DAYS = "SELECT lessons_days FROM group_lessons_days where group_entity_id = :group_id";
	public static final String GROUP_WEBINARS_DAYS = "SELECT webinars_days FROM group_webinars_days where group_entity_id = :group_id";
	
	GroupEntity findByName(String name);

	List<GroupEntity> findByStatus(GroupStatus status);
	
	@Modifying
	@Query(value = UPDATE_STUDENT_GROUP, nativeQuery = true)
	public int updateStudentGroups(@Param("student_id") long student_id, @Param("id") Long id,
			@Param("old_id") Long old_id);
	
	@Query(value = GROUP_LESSONS_DAYS, nativeQuery = true)
	public List<WeekDay> getLessonsDays(@Param("group_id") Long group_id);
	
	@Query(value = GROUP_LESSONS_DAYS, nativeQuery = true)
	public List<WeekDay> getWebinarsDays(@Param("group_id") Long group_id);
}
