package edutrack.schedule.service;

import edutrack.schedule.dto.request.ScheduleCreateRequest;
import edutrack.schedule.dto.request.ScheduleUpdateDataRequest;
import edutrack.schedule.dto.response.ScheduleResponse;
import edutrack.schedule.dto.response.SingleScheduleResponse;

import org.springframework.stereotype.Service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class StudentScheduleService implements ScheduleService {

	@Override
	public ScheduleResponse getAllSchedulers(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ScheduleResponse addSchedule(ScheduleCreateRequest schedule) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ScheduleResponse updateSchedule(ScheduleUpdateDataRequest schedule) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean deleteSchedule(Long scheduleId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean deleteAllSchedulers(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SingleScheduleResponse getSchedule(Long scheduleId) {
		// TODO Auto-generated method stub
		return null;
	}

}
