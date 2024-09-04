package edutrack.repository;
import edutrack.entity.students.Group;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends JpaRepository<Group, String> {

	Group findByName(String name);
}
