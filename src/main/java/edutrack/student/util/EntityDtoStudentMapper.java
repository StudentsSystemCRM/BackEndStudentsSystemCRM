package edutrack.student.util;

import java.time.LocalDateTime;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import edutrack.student.dto.request.StudentCreateRequest;
import edutrack.student.dto.response.StudentDataResponse;
import edutrack.student.entity.StudentEntity;

@Mapper(imports = {LocalDateTime.class})
public interface EntityDtoStudentMapper {
	EntityDtoStudentMapper INSTANCE = Mappers.getMapper(EntityDtoStudentMapper.class);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "activityLogs", ignore = true)
	@Mapping(target = "payments", ignore = true)
	@Mapping(target = "groups", ignore = true)
	@Mapping(target = "studentSchedulers", ignore = true)
	@Mapping(target = "totalSumToPay", ignore = true)
	@Mapping(target = "originalGroupId", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "createdDate", expression = "java(LocalDateTime.now())")
	@Mapping(target = "lastModifiedBy", ignore = true)
	@Mapping(target = "lastModifiedDate", ignore = true)
	StudentEntity studentCreateRequestToStudent(StudentCreateRequest studentCreate);

	StudentDataResponse studentToStudentDataResponse(StudentEntity studentEntity);

}
