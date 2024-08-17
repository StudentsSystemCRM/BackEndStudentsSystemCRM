package edutrack.util;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import edutrack.dto.request.accounting.UserRegisterRequest;
import edutrack.dto.response.accounting.LoginSuccessResponse;
import edutrack.dto.response.accounting.UserDataResponse;
import edutrack.entity.accounting.User;

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
}
