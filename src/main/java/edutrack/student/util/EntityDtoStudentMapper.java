package edutrack.student.util;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import edutrack.student.dto.request.StudentCreateRequest;
import edutrack.student.dto.response.StudentDataResponse;
import edutrack.student.entity.StudentEntity;

@Mapper
public interface EntityDtoStudentMapper {
	EntityDtoStudentMapper INSTANCE = Mappers.getMapper(EntityDtoStudentMapper.class);

	@Mapping(source = "name", target = "firstName")
	@Mapping(source = "surname", target = "lastName")
	@Mapping(source = "phone", target = "phoneNumber")
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "activityLogs", ignore = true)
	@Mapping(target = "payments", ignore = true)
	@Mapping(target = "groups", ignore = true)
	@Mapping(target = "studentReminders", ignore = true)
	@Mapping(target = "totalSumToPay", ignore = true)
	@Mapping(target = "originalGroup", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "lastModifiedBy", ignore = true)
	StudentEntity studentCreateRequestToStudent(StudentCreateRequest studentCreate);

	@Mapping(source = "firstName", target = "name")
	@Mapping(source = "lastName", target = "surname")
	@Mapping(source = "phoneNumber", target = "phone")
	StudentDataResponse studentToStudentDataResponse(StudentEntity studentEntity);

}
