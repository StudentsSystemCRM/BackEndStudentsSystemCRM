package edutrack.schedule.util;

import java.util.List;

import edutrack.group.entity.GroupEntity;
import edutrack.schedule.dto.response.StudentScheduleResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import edutrack.schedule.dto.response.GroupScheduleResponse;
import edutrack.schedule.dto.response.SingleScheduleResponse;
import edutrack.student.entity.StudentEntity;

@Mapper
public interface EntityDtoScheduleMapper {
	
	EntityDtoScheduleMapper INSTANCE = Mappers.getMapper(EntityDtoScheduleMapper.class);
	
	@Mapping(target = "reminders", source = "studentReminders")
    StudentScheduleResponse studentToReminderResponse(StudentEntity student, List<SingleScheduleResponse> studentReminders);

	@Mapping(target = "reminders", source = "groupReminders")
    GroupScheduleResponse groupToReminderResponse(GroupEntity group, List<SingleScheduleResponse> groupReminders);

}
