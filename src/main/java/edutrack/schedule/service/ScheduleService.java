package edutrack.schedule.service;

import java.util.List;

import edutrack.schedule.constant.SheduleType;
import edutrack.schedule.dto.request.ScheduleCreateRequest;
import edutrack.schedule.dto.request.ScheduleUpdateDataRequest;
import edutrack.schedule.dto.response.ScheduleResponse;
import edutrack.schedule.dto.response.SingleScheduleResponse;

public interface ScheduleService {

	ScheduleResponse getSchedule(Long scheduleId);
	ScheduleResponse getAllSchedulers(Long id);
	List<SingleScheduleResponse> getSchedulersBySheduleType(SheduleType sheduleType);

	ScheduleResponse addSchedule(ScheduleCreateRequest schedule);
	
	ScheduleResponse updateSchedule(ScheduleUpdateDataRequest schedule);
	
	Boolean deleteSchedule(Long scheduleId);
	Boolean deleteAllSchedulers(Long id);

}
