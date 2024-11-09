package edutrack.group.repository;
import edutrack.group.constant.GroupStatus;
import edutrack.group.entity.GroupEntity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends JpaRepository<GroupEntity, Long> {
	
	public static final String GROUP_STUDENTS_IDS = "SELECT students_id FROM groups_students WHERE group_id=:group_id";
	
	GroupEntity findByName(String name);
	List<GroupEntity> findByNameContainingIgnoreCase(String name);
	List<GroupEntity> findByStatus(GroupStatus status);
	
	@Query(value = GROUP_STUDENTS_IDS, nativeQuery = true)
	public List<Long> findStudentsIdsByGroup(@Param("group_id") Long group_id);
}
