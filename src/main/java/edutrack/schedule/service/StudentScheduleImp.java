package edutrack.schedule.service;

import edutrack.schedule.dto.request.ScheduleCreateRequest;
import edutrack.schedule.dto.request.ScheduleUpdateDataRequest;
import edutrack.schedule.dto.response.ScheduleResponse;
import org.springframework.stereotype.Service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class StudentScheduleImp implements StudentScheduleService {

	@Override
	public ScheduleResponse getAllReminders(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ScheduleResponse addReminder(ScheduleCreateRequest reminder) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ScheduleResponse updateReminder(ScheduleUpdateDataRequest reminder) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean deleteReminder(Long id, Long scheduleId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean deleteAllReminders(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

}
