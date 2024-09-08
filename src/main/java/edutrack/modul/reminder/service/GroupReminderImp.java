package edutrack.modul.reminder.service;

import org.springframework.stereotype.Service;

import edutrack.modul.reminder.dto.request.AddGroupReminderRequest;
import edutrack.modul.reminder.dto.response.ReminderResponse;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class GroupReminderImp implements GroupReminderService {

	@Override
	@Transactional
	public ReminderResponse getGroupReminders(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Transactional
	public ReminderResponse addGroupReminder(AddGroupReminderRequest groupReminder) {
		// TODO Auto-generated method stub
		return null;
	}
}
