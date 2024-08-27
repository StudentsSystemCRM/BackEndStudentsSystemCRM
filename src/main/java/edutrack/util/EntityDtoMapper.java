package edutrack.util;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import edutrack.dto.request.accounting.UserRegisterRequest;
import edutrack.dto.request.students.StudentCreateRequest;
import edutrack.dto.response.accounting.LoginSuccessResponse;
import edutrack.dto.response.accounting.UserDataResponse;
import edutrack.dto.response.students.StudentActivityLog;
import edutrack.dto.response.students.StudentDataResponse;
import edutrack.entity.accounting.User;
import edutrack.entity.students.ActivityLog;
import edutrack.entity.students.Student;

@Mapper
public interface EntityDtoMapper {
	EntityDtoMapper INSTANCE = Mappers.getMapper(EntityDtoMapper.class);

	@Mapping(target = "id", expression = "java(java.util.UUID.randomUUID().toString())")
	@Mapping(target = "hashedPassword", ignore = true)
	@Mapping(target = "createdDate", expression = "java(java.time.LocalDate.now())")
	@Mapping(target = "roles", ignore = true)
	User userRegisterRequestToUser(UserRegisterRequest userRegisterRequest);

	@Mapping(target = "token", ignore = true)
	LoginSuccessResponse userToLoginSuccessResponse(User user);
	
	UserDataResponse userToUserDataResponse(User user);
	
	
	
	//student
	
    @Mapping(source = "name", target = "firstName")
    @Mapping(source = "surname", target = "lastName")
    @Mapping(source = "phone", target = "phoneNumber")
    @Mapping(target = "activityLogs", ignore = true)
    @Mapping(target = "payments", ignore = true)
    @Mapping(target = "group", ignore = true)
    @Mapping(target = "id", ignore = true)
	Student studentCreateRequestToStudent(StudentCreateRequest studentCreate);

    @Mapping(source = "firstName", target = "name")
    @Mapping(source = "lastName", target = "surname")
    @Mapping(source = "phoneNumber", target = "phone")
	StudentDataResponse studentToStudentDataResponse(Student studentEntity);

    @Mapping(source = "information", target = "message")
	StudentActivityLog activityLogEntitytoStudentActivityLog(ActivityLog activityLog);
}
