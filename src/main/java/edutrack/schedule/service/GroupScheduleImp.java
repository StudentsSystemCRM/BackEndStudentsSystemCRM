package edutrack.schedule.service;

import edutrack.group.entity.GroupEntity;
import edutrack.group.exception.GroupNotFoundException;
import edutrack.group.repository.GroupRepository;
import edutrack.schedule.dto.request.AddGroupScheduleRequest;
import edutrack.schedule.dto.response.GroupScheduleResponse;
import edutrack.schedule.dto.response.SingleScheduleResponse;
import edutrack.schedule.entity.GroupScheduleEntity;
import edutrack.schedule.repository.GroupScheduleRepository;
import edutrack.schedule.util.EntityDtoScheduleMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class GroupScheduleImp implements GroupScheduleService {

	GroupScheduleRepository groupRemindersRepository;
	GroupRepository groupRepository;
	
	@Override
	@Transactional
	public GroupScheduleResponse getGroupReminders(String name) {
		GroupEntity groupEntity = groupRepository.findByName(name);
        if (groupEntity == null)
            throw new GroupNotFoundException("Group with name " + name + "  not found");
    List<GroupScheduleEntity> groupReminders = groupRemindersRepository.findByGroupName(name);
    List<SingleScheduleResponse> reminders = groupReminders.stream()
    	    .map(reminder -> new SingleScheduleResponse(reminder.getId(), reminder.getDateTime(), reminder.getComment()))
    	    .collect(Collectors.toList());
    return EntityDtoScheduleMapper.INSTANCE.groupToReminderResponse(groupEntity, reminders);
	}

	@Override
	@Transactional
	public GroupScheduleResponse addGroupReminder(AddGroupScheduleRequest groupReminder) {
        GroupEntity group = groupRepository.findByName(groupReminder.getName());
        if (group == null)
            throw new GroupNotFoundException("Group with name " + groupReminder.getName() + "  not found");
        LocalDateTime dateTime = groupReminder.getDateTime()==null?LocalDateTime.now():groupReminder.getDateTime();
        GroupScheduleEntity groupReminders = new GroupScheduleEntity(null, dateTime, groupReminder.getComment(), group);
        groupReminders = groupRemindersRepository.save(groupReminders);
        return getGroupReminders(groupReminder.getName());
	}
}
