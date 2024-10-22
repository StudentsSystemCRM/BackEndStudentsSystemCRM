package edutrack.student.repository;
import java.util.List;

import edutrack.student.constant.LeadStatus;
import edutrack.student.entity.StudentEntity;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<StudentEntity, Long> {
	public static final String STUDENT_GROUPS_IDS = "SELECT group_id FROM groups_students WHERE students_id = :students_id";
	public static final String DELETE_STUDENT_GROUP = "DELETE groups_students WHERE students_id = :students_id and group_id=:group_id";
	public static final String UPDATE_STUDENT_GROUP = "UPDATE groups_students SET group_id=:group_id where students_id = :students_id and group_id=:old_group_id";
	public static final String ADD_STUDENT_TO_GROUP = "INSERT INTO groups_students VALUES (:group_id, :students_id)";
	public static final String GROUP_EXISTS_BY_ID = "SELECT id FROM groups WHERE id = :group_id";
	
	@Query(value = STUDENT_GROUPS_IDS, nativeQuery = true)
	public List<Long> findStudentGroupsIds(@Param("students_id") Long students_id);
	
	@Modifying
	@Query(value = ADD_STUDENT_TO_GROUP, nativeQuery = true)
	public Integer addStudentToGroup(@Param("students_id") Long students_id, @Param("group_id") Long group_id);
	
	@Modifying
	@Query(value = DELETE_STUDENT_GROUP, nativeQuery = true)
	public Integer deleteStudentFromGroup(@Param("students_id") Long students_id, @Param("group_id") Long group_id);
	
	@Modifying
	@Query(value = UPDATE_STUDENT_GROUP, nativeQuery = true)
	public int updateStudentGroups(@Param("students_id") Long students_id, @Param("group_id") Long group_id,
			@Param("old_group_id") Long old_group_id);
	
	@Query(value = GROUP_EXISTS_BY_ID, nativeQuery = true)
	public List<Long> groupExistsById(@Param("group_id") Long group_id);
	
	StudentEntity findByEmail(String email);
	List<StudentEntity> findByFirstNameContainingIgnoreCase(Pageable pageable, String name);
	List<StudentEntity> findByLastNameContainingIgnoreCase(Pageable pageable, String name);
	List<StudentEntity> findByFirstNameContainingIgnoreCaseAndLastNameContainingIgnoreCase(Pageable pageable, String name, String surname);
	List<StudentEntity> findByLeadStatus(Pageable pageable, LeadStatus status);
}
