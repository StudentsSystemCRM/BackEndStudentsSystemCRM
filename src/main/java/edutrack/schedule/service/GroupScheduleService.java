package edutrack.schedule.service;

import edutrack.schedule.dto.request.AddGroupScheduleRequest;
import edutrack.schedule.dto.response.GroupScheduleResponse;

public interface GroupScheduleService {

	GroupScheduleResponse getGroupReminders(String name);

	GroupScheduleResponse addGroupReminder(AddGroupScheduleRequest groupReminder);

}
