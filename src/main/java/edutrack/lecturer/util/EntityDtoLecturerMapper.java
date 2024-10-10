package edutrack.lecturer.util;

import edutrack.group.entity.GroupEntity;
import edutrack.lecturer.dto.request.LecturerCreateRequest;
import edutrack.lecturer.dto.request.LecturerUpdateRequest;
import edutrack.lecturer.dto.response.LecturerDataResponse;
import edutrack.lecturer.entity.LecturerEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.Set;
import java.util.stream.Collectors;
@Mapper(componentModel = "spring")
public interface EntityDtoLecturerMapper {
	EntityDtoLecturerMapper INSTANCE = Mappers.getMapper(EntityDtoLecturerMapper.class);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "lastModifiedBy", ignore = true)
	LecturerEntity toLecturerEntity(LecturerCreateRequest request, Set<GroupEntity> groups);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "lastModifiedBy", ignore = true)
	void updateLecturerFromRequest(LecturerUpdateRequest request, @MappingTarget LecturerEntity lecturer);

	default LecturerDataResponse toLecturerDataResponse(LecturerEntity lecturer, Set<GroupEntity> groups) {
		Set<Long> groupIds = groups.stream()
				.map(GroupEntity::getId)
				.collect(Collectors.toSet());

		return new LecturerDataResponse(
				lecturer.getId(),
				lecturer.getFirstName(),
				lecturer.getLastName(),
				lecturer.getPhoneNumber(),
				lecturer.getEmail(),
				lecturer.getCity(),
				lecturer.getStatus(),
				groupIds
		);
	}
}




