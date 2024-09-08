package edutrack.modul.reminder.service;

import edutrack.modul.reminder.dto.request.AddGroupReminderRequest;
import edutrack.modul.reminder.dto.response.ReminderResponse;

public interface GroupReminderService {

	ReminderResponse getGroupReminders(String name);

	ReminderResponse addGroupReminder(AddGroupReminderRequest groupReminder);

}
