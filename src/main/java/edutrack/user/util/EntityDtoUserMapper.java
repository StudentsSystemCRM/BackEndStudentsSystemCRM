package edutrack.user.util;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import edutrack.user.dto.request.UserRegisterRequest;
import edutrack.user.dto.response.LoginSuccessResponse;
import edutrack.user.dto.response.UserDataResponse;
import edutrack.user.entity.UserEntity;

@Mapper
public interface EntityDtoUserMapper {

	EntityDtoUserMapper INSTANCE = Mappers.getMapper(EntityDtoUserMapper.class);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "hashedPassword", ignore = true)
	@Mapping(target = "createdDate", expression = "java(java.time.LocalDate.now())")
	@Mapping(target = "roles", ignore = true)
	UserEntity userRegisterRequestToUser(UserRegisterRequest userRegisterRequest);

	@Mapping(target = "token", ignore = true)
	LoginSuccessResponse userToLoginSuccessResponse(UserEntity user);

	UserDataResponse userToUserDataResponse(UserEntity user);

}
