package edutrack.modul.reminder.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import edutrack.exception.StudentNotFoundException;
import edutrack.modul.reminder.dto.request.AddStudentReminderRequest;
import edutrack.modul.reminder.dto.response.ReminderResponse;
import edutrack.modul.reminder.dto.response.SingleReminder;
import edutrack.modul.reminder.entity.StudentReminders;
import edutrack.modul.reminder.repository.StudentRemindersRepository;
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
public class StudentReminderImp implements StudentReminderService {
	
	StudentRemindersRepository studentRemindersRepository;
	StudentRepository studentRepository;

	@Override
	@Transactional
	public ReminderResponse getStudentReminders(Long id) {
        Student student = studentRepository.findById(id)
        			.orElseThrow(() -> new StudentNotFoundException("Student with id " + id + " not found"));
        List<StudentReminders> studentReminders = studentRemindersRepository.findByStudentId(id);
        List<SingleReminder> reminders = studentReminders.stream()
        	    .map(reminder -> new SingleReminder(reminder.getId(), reminder.getDateTime(), reminder.getComment()))
        	    .collect(Collectors.toList());
        return EntityDtoMapper.INSTANCE.studentToReminderResponse(student, reminders);
	}

	@Override
	@Transactional
	public ReminderResponse addStudentReminder(AddStudentReminderRequest studentReminder) {
        Student student = studentRepository.findById(studentReminder.getStudentId())
        		.orElseThrow(() -> new StudentNotFoundException("Student with id " + studentReminder.getStudentId() + " not found"));
        LocalDateTime dateTime = studentReminder.getDateTime()==null?LocalDateTime.now():studentReminder.getDateTime();
        StudentReminders studentReminders = new StudentReminders(null, dateTime, studentReminder.getComment(), student);
        studentReminders = studentRemindersRepository.save(studentReminders);
        return getStudentReminders(studentReminder.getStudentId());
	}

}
