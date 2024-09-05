package edutrack.modul.activityLog.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edutrack.modul.activityLog.entity.ActivityLog;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

	List<ActivityLog> findByStudentId (Long id);

	void deleteByStudentId(Long id);
}
