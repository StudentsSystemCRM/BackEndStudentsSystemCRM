package edutrack.modul.group.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edutrack.modul.group.entity.Group;

@Repository
public interface GroupRepository extends JpaRepository<Group, String> {

	Group findByName(String name);
}
