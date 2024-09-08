package edutrack.modul.reminder.service;

import edutrack.modul.reminder.dto.request.AddStudentReminderRequest;
import edutrack.modul.reminder.dto.response.ReminderResponse;

public interface StudentReminderService {

	ReminderResponse getStudentReminders(Long id);

	ReminderResponse addStudentReminder(AddStudentReminderRequest studentReminder);

}
