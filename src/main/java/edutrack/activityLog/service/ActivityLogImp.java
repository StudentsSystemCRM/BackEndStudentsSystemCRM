package edutrack.activityLog.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import edutrack.activityLog.dto.response.ActivityLogResponse;
import edutrack.activityLog.dto.response.SingleActivityLog;
import edutrack.activityLog.repository.ActivityLogRepository;
import edutrack.activityLog.util.EntityDtoActivityLogMapper;

import org.springframework.stereotype.Service;

import edutrack.exception.StudentNotFoundException;
import edutrack.activityLog.dto.request.AddActivityLogRequest;
import edutrack.activityLog.entity.ActivityLogEntity;
import edutrack.student.entity.StudentEntity;
import edutrack.student.repository.StudentRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ActivityLogImp implements ActivityLogService {

	ActivityLogRepository logRepository;
	StudentRepository studentRepository;

	@Override
	@Transactional
	public ActivityLogResponse getStudentActivityLog(Long id) {
		StudentEntity student = studentRepository.findById(id)
				.orElseThrow(() -> new StudentNotFoundException("Student with id " + id + " not found"));
		List<ActivityLogEntity> activityLogs = logRepository.findByStudentId(id);
		List<SingleActivityLog> studentActivityLog = activityLogs.stream()
				.map(log -> new SingleActivityLog(log.getId(), log.getDate(), log.getInformation()))
				.collect(Collectors.toList());
		return EntityDtoActivityLogMapper.INSTANCE.studentToActivityLogResponse(student, studentActivityLog);
	}

	@Override
	@Transactional
	public ActivityLogResponse addActivityLog(AddActivityLogRequest studentComment) {
		StudentEntity student = studentRepository.findById(studentComment.getStudentId()).orElseThrow(
				() -> new StudentNotFoundException("Student with id " + studentComment.getStudentId() + " not found"));
		LocalDate date = studentComment.getDate() == null ? LocalDate.now() : studentComment.getDate();
		ActivityLogEntity activityLog = new ActivityLogEntity(null, date, studentComment.getMessage(), student);
		activityLog = logRepository.save(activityLog);
		return getStudentActivityLog(studentComment.getStudentId());
	}
}
