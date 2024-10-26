package edutrack.schedule.util;

import java.time.ZonedDateTime;
import java.util.List;

import edutrack.schedule.dto.request.ScheduleCreateRequest;
import edutrack.schedule.dto.response.ScheduleResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import edutrack.schedule.dto.response.SingleScheduleResponse;
import edutrack.schedule.entity.GroupScheduleEntity;
import edutrack.schedule.entity.StudentScheduleEntity;

@Mapper(imports = {ZonedDateTime.class})
public interface EntityDtoScheduleMapper {
	
	EntityDtoScheduleMapper INSTANCE = Mappers.getMapper(EntityDtoScheduleMapper.class);
	
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "createdDate", expression = "java(ZonedDateTime.now())")
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "group", ignore = true)
    @Mapping(target = "scheduleId", ignore = true)
	GroupScheduleEntity groupScheduleCreateRequestToGroupScheduleEntity(ScheduleCreateRequest scheduleCreateRequest);
	
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "createdDate", expression = "java(ZonedDateTime.now())")
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "student", ignore = true)
    @Mapping(target = "scheduleId", ignore = true)
	StudentScheduleEntity studentScheduleCreateRequestToStudentScheduleEntity(ScheduleCreateRequest scheduleCreateRequest);
	
    SingleScheduleResponse groupScheduleEntityToSingleScheduleResponse(GroupScheduleEntity groupScheduleEntity);
    
	SingleScheduleResponse studentScheduleEntityToSingleScheduleResponse(StudentScheduleEntity studentScheduleEntity);
    
    ScheduleResponse singleScheduleResponseToSheduleResponse(Long id, List<SingleScheduleResponse> schedulers);

}
