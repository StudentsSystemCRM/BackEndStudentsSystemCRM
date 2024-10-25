package edutrack.schedule.service;

import edutrack.schedule.dto.request.ScheduleCreateRequest;
import edutrack.schedule.dto.request.ScheduleUpdateDataRequest;
import edutrack.schedule.dto.response.ScheduleResponse;
import edutrack.schedule.dto.response.SingleScheduleResponse;

public interface ScheduleService {

	SingleScheduleResponse getSchedule(Long scheduleId);
	ScheduleResponse getAllSchedulers(Long id);

	ScheduleResponse addSchedule(ScheduleCreateRequest schedule);
	
	ScheduleResponse updateSchedule(ScheduleUpdateDataRequest schedule);
	
	Boolean deleteSchedule(Long scheduleId);
	Boolean deleteAllSchedulers(Long id);

}
