package edutrack.modul.activityLog.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import edutrack.exception.StudentNotFoundException;
import edutrack.modul.activityLog.dto.request.AddActivityLogRequest;
import edutrack.modul.activityLog.dto.response.ActivityLogResponse;
import edutrack.modul.activityLog.dto.response.SingleActivityLog;
import edutrack.modul.activityLog.entity.ActivityLog;
import edutrack.modul.activityLog.repository.ActivityLogRepository;
import edutrack.modul.student.entity.Student;
import edutrack.modul.student.repository.StudentRepository;
import edutrack.util.EntityDtoMapper;
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
        Student student = studentRepository.findById(id)
        			.orElseThrow(() -> new StudentNotFoundException("Student with id " + id + " not found"));
        List<ActivityLog> activityLogs = logRepository.findByStudentId(id);
        List<SingleActivityLog> studentActivityLog = activityLogs.stream()
        	    .map(log -> new SingleActivityLog(log.getId(), log.getDate(), log.getInformation()))
        	    .collect(Collectors.toList());
        return EntityDtoMapper.INSTANCE.studentToActivityLogResponse(student, studentActivityLog);
	}

	@Override
	@Transactional
	public ActivityLogResponse addActivityLog(AddActivityLogRequest studentComment) {
        Student student = studentRepository.findById(studentComment.getStudentId())
        		.orElseThrow(() -> new StudentNotFoundException("Student with id " + studentComment.getStudentId() + " not found"));
        LocalDate date = studentComment.getDate()==null?LocalDate.now():studentComment.getDate();
        ActivityLog activityLog = new ActivityLog(null, date, studentComment.getMessage(), student);
        activityLog = logRepository.save(activityLog);
        return getStudentActivityLog(studentComment.getStudentId());
	}
}
