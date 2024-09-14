package edutrack.schedule.service;

import edutrack.schedule.dto.request.AddStudentScheduleRequest;
import edutrack.schedule.dto.response.ScheduleResponse;

public interface StudentScheduleService {

	ScheduleResponse getStudentReminders(Long id);

	ScheduleResponse addStudentReminder(AddStudentScheduleRequest studentReminder);

}
