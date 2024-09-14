package edutrack.reminder.util;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import edutrack.reminder.dto.response.ScheduleResponse;
import edutrack.reminder.dto.response.SingleScheduleResponse;
import edutrack.student.entity.StudentEntity;

@Mapper
public interface EntityDtoScheduleMapper {
	
	EntityDtoScheduleMapper INSTANCE = Mappers.getMapper(EntityDtoScheduleMapper.class);
	
	@Mapping(target = "reminders", source = "studentReminders")
    ScheduleResponse studentToReminderResponse(StudentEntity student, List<SingleScheduleResponse> studentReminders);


}
