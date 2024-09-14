package edutrack.activityLog.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edutrack.activityLog.entity.ActivityLogEntity;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLogEntity, Long> {

	List<ActivityLogEntity> findByStudentId (Long id);

	void deleteByStudentId(Long id);
}
