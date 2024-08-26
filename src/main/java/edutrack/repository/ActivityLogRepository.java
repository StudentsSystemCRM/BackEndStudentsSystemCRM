package edutrack.repository;

import edutrack.entity.students.ActivityLog;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

	List<ActivityLog> findByStudentId (Long id);

	void deleteByStudentId(Long id);
}
