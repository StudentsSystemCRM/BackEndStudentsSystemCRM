package edutrack.schedule.repository;

import java.util.List;

import edutrack.schedule.entity.StudentScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentScheduleRepository extends JpaRepository<StudentScheduleEntity, Long> {
	
	List<StudentScheduleEntity> findByStudentId (Long id);
}
