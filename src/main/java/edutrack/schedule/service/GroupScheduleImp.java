package edutrack.schedule.service;

import edutrack.schedule.dto.request.AddGroupScheduleRequest;
import edutrack.schedule.dto.response.ScheduleResponse;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class GroupScheduleImp implements GroupScheduleService {

	@Override
	@Transactional
	public ScheduleResponse getGroupReminders(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Transactional
	public ScheduleResponse addGroupReminder(AddGroupScheduleRequest groupReminder) {
		// TODO Auto-generated method stub
		return null;
	}
}
