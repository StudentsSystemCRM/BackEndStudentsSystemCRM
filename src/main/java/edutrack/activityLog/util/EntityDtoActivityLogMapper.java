package edutrack.activityLog.util;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import edutrack.activityLog.dto.response.ActivityLogResponse;
import edutrack.activityLog.dto.response.SingleActivityLog;
import edutrack.activityLog.entity.ActivityLogEntity;
import edutrack.student.entity.StudentEntity;

@Mapper
public interface EntityDtoActivityLogMapper {

	EntityDtoActivityLogMapper INSTANCE = Mappers.getMapper(EntityDtoActivityLogMapper.class);

	@Mapping(source = "information", target = "message")
	SingleActivityLog activityLogEntitytoStudentActivityLog(ActivityLogEntity activityLog);

	@Mapping(target = "activityLogs", source = "studentActivityLog")
	ActivityLogResponse studentToActivityLogResponse(StudentEntity student, List<SingleActivityLog> studentActivityLog);

}
