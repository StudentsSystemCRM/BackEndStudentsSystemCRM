package edutrack.schedule.service;

import edutrack.schedule.dto.request.AddStudentScheduleRequest;
import edutrack.schedule.dto.response.StudentScheduleResponse;

public interface StudentScheduleService {

	StudentScheduleResponse getStudentReminders(Long id);

	StudentScheduleResponse addStudentReminder(AddStudentScheduleRequest studentReminder);

}
