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
public interface GroupRepository extends JpaRepository<GroupEntity, String> {

	public static final String UPDATE_STUDENT_GROUP = "UPDATE groups_students SET group_name=:name where students_id = :id and group_name=:old_name";
	public static final String GROUP_LESSONS_DAYS = "SELECT lessons_days FROM group_lessons_days where group_entity_name = :group_name";
	public static final String GROUP_WEBINARS_DAYS = "SELECT webinars_days FROM group_webinars_days where group_entity_name = :group_name";
	
	GroupEntity findByName(String name);

	List<GroupEntity> findByStatus(GroupStatus status);
	
	@Modifying
	@Query(value = UPDATE_STUDENT_GROUP, nativeQuery = true)
	public int updateStudentGroups(@Param("id") long id, @Param("name") String name,@Param("old_name") String old_name);
	
	@Query(value = GROUP_LESSONS_DAYS, nativeQuery = true)
	public List<WeekDay> getLessonsDays(@Param("group_name") String group_name);
	
	@Query(value = GROUP_LESSONS_DAYS, nativeQuery = true)
	public List<WeekDay> getWebinarsDays(@Param("group_name") String group_name);
}
