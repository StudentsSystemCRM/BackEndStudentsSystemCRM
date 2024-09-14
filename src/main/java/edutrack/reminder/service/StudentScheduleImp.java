package edutrack.reminder.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import edutrack.reminder.dto.request.AddStudentScheduleRequest;
import edutrack.reminder.dto.response.ScheduleResponse;
import edutrack.reminder.dto.response.SingleScheduleResponse;
import edutrack.reminder.entity.StudentScheduleEntity;
import org.springframework.stereotype.Service;

import edutrack.exception.StudentNotFoundException;
import edutrack.reminder.repository.StudentScheduleRepository;
import edutrack.reminder.util.EntityDtoScheduleMapper;
import edutrack.student.entity.StudentEntity;
import edutrack.student.repository.StudentRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class StudentScheduleImp implements StudentScheduleService {
	
	StudentScheduleRepository studentRemindersRepository;
	StudentRepository studentRepository;

	@Override
	@Transactional
	public ScheduleResponse getStudentReminders(Long id) {
        StudentEntity student = studentRepository.findById(id)
        			.orElseThrow(() -> new StudentNotFoundException("Student with id " + id + " not found"));
        List<StudentScheduleEntity> studentReminders = studentRemindersRepository.findByStudentId(id);
        List<SingleScheduleResponse> reminders = studentReminders.stream()
        	    .map(reminder -> new SingleScheduleResponse(reminder.getId(), reminder.getDateTime(), reminder.getComment()))
        	    .collect(Collectors.toList());
        return EntityDtoScheduleMapper.INSTANCE.studentToReminderResponse(student, reminders);
	}

	@Override
	@Transactional
	public ScheduleResponse addStudentReminder(AddStudentScheduleRequest studentReminder) {
        StudentEntity student = studentRepository.findById(studentReminder.getStudentId())
        		.orElseThrow(() -> new StudentNotFoundException("Student with id " + studentReminder.getStudentId() + " not found"));
        LocalDateTime dateTime = studentReminder.getDateTime()==null?LocalDateTime.now():studentReminder.getDateTime();
        StudentScheduleEntity studentReminders = new StudentScheduleEntity(null, dateTime, studentReminder.getComment(), student);
        studentReminders = studentRemindersRepository.save(studentReminders);
        return getStudentReminders(studentReminder.getStudentId());
	}

}
