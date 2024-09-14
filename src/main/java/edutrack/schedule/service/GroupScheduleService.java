package edutrack.schedule.service;

import edutrack.schedule.dto.request.AddGroupScheduleRequest;
import edutrack.schedule.dto.response.ScheduleResponse;

public interface GroupScheduleService {

	ScheduleResponse getGroupReminders(String name);

	ScheduleResponse addGroupReminder(AddGroupScheduleRequest groupReminder);

}
