package edutrack.student.util;

import java.time.LocalDateTime;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import edutrack.group.constant.GroupStatus;
import edutrack.student.dto.request.StudentCreateRequest;
import edutrack.student.dto.response.StudentDataResponse;
import edutrack.student.entity.StudentEntity;

@Mapper(imports = {LocalDateTime.class})
public interface EntityDtoStudentMapper {
	EntityDtoStudentMapper INSTANCE = Mappers.getMapper(EntityDtoStudentMapper.class);

	@Mapping(source = "name", target = "firstName")
	@Mapping(source = "surname", target = "lastName")
	@Mapping(source = "phone", target = "phoneNumber")
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "activityLogs", ignore = true)
	@Mapping(target = "payments", ignore = true)
	@Mapping(target = "groups", ignore = true)
	@Mapping(target = "studentShedulers", ignore = true)
	@Mapping(target = "totalSumToPay", ignore = true)
	@Mapping(target = "originalGroupId", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "createdDate", expression="java(LocalDateTime.now())")
	@Mapping(target = "lastModifiedBy", ignore = true)
	StudentEntity studentCreateRequestToStudent(StudentCreateRequest studentCreate);

	@Mapping(source = "firstName", target = "name")
	@Mapping(source = "lastName", target = "surname")
	@Mapping(source = "phoneNumber", target = "phone")
	StudentDataResponse studentToStudentDataResponse(StudentEntity studentEntity);

}
