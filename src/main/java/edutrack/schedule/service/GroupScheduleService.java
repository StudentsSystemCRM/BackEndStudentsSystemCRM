package edutrack.schedule.service;

import edutrack.schedule.dto.request.ScheduleCreateRequest;
import edutrack.schedule.dto.request.ScheduleUpdateDataRequest;
import edutrack.schedule.dto.response.ScheduleResponse;

public interface GroupScheduleService {

	ScheduleResponse getAllReminders(Long id);

	ScheduleResponse addReminder(ScheduleCreateRequest reminder);
	
	ScheduleResponse updateReminder(ScheduleUpdateDataRequest reminder);
	
	Boolean deleteReminder(Long id, Long scheduleId);
	Boolean deleteAllReminders(Long id);
}
