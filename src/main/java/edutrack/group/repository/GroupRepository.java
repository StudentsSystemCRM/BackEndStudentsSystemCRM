package edutrack.group.repository;
import edutrack.group.constant.GroupStatus;
import edutrack.group.constant.WeekDay;
import edutrack.group.entity.GroupEntity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends JpaRepository<GroupEntity, String> {

//	public static final String STUDENT_GROUPS = "SELECT group_name FROM groups_students where students_id = :student_id";
//	public static final String ALL_STUDENT_GROUPS = "SELECT * FROM groups where name in (:all_student_groups)";
	public static final String GROUP_LESSONS_DAYS = "SELECT lessons_days FROM group_lessons_days where group_entity_name = :group_name";
	public static final String GROUP_WEBINARS_DAYS = "SELECT webinars_days FROM group_webinars_days where group_entity_name = :group_name";
	
	GroupEntity findByName(String name);

	List<GroupEntity> findByStatus(GroupStatus status);
	
//	@Query(value = ALL_STUDENT_GROUPS, nativeQuery = true)
//	public List<GroupEntity> findAllStudentGroups(@Param("all_student_groups") List<String> all_student_groups);
//	
//	@Query(value = STUDENT_GROUPS, nativeQuery = true)
//	public List<String> getGroups(@Param("student_id") long student_id);
	
	@Query(value = GROUP_LESSONS_DAYS, nativeQuery = true)
	public List<WeekDay> getLessonsDays(@Param("group_name") String group_name);
	
	@Query(value = GROUP_LESSONS_DAYS, nativeQuery = true)
	public List<WeekDay> getWebinarsDays(@Param("group_name") String group_name);
}
