package edutrack.group.repository;
import edutrack.group.constant.GroupStatus;
import edutrack.group.entity.GroupEntity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends JpaRepository<GroupEntity, Long> {

	GroupEntity findByName(String name);
	List<GroupEntity> findByNameContainingIgnoreCase(String name);
	List<GroupEntity> findByStatus(GroupStatus status);

}
