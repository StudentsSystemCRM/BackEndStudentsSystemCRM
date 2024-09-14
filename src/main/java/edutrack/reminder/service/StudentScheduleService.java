package edutrack.reminder.service;

import edutrack.reminder.dto.request.AddStudentScheduleRequest;
import edutrack.reminder.dto.response.ScheduleResponse;

public interface StudentScheduleService {

	ScheduleResponse getStudentReminders(Long id);

	ScheduleResponse addStudentReminder(AddStudentScheduleRequest studentReminder);

}
