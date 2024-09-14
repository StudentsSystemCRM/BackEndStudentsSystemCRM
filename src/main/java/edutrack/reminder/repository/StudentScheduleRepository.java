package edutrack.reminder.repository;

import java.util.List;

import edutrack.reminder.entity.StudentScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentScheduleRepository extends JpaRepository<StudentScheduleEntity, Long> {
	
	List<StudentScheduleEntity> findByStudentId (Long id);
}
