package edutrack.reminder.service;

import edutrack.reminder.dto.request.AddGroupScheduleRequest;
import edutrack.reminder.dto.response.ScheduleResponse;

public interface GroupScheduleService {

	ScheduleResponse getGroupReminders(String name);

	ScheduleResponse addGroupReminder(AddGroupScheduleRequest groupReminder);

}
