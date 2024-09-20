package edutrack.lecturer.util;

import edutrack.lecturer.dto.request.LecturerCreateRequest;
import edutrack.lecturer.dto.request.LecturerUpdateRequest;
import edutrack.lecturer.dto.response.LecturerDataResponse;
import edutrack.lecturer.entity.LecturerEntity;
import edutrack.group.entity.GroupEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper
public interface EntityDtoLecturerMapper {
	EntityDtoLecturerMapper INSTANCE = Mappers.getMapper(EntityDtoLecturerMapper.class);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "lastModifiedBy", ignore = true)
	@Mapping(target = "groups", ignore = true)
	LecturerEntity lecturerCreateRequestToLecturer(LecturerCreateRequest request);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "lastModifiedBy", ignore = true)
	@Mapping(target = "groups", ignore = true)
	void updateLecturerFromRequest(LecturerUpdateRequest request, @MappingTarget LecturerEntity lecturer);

	@Mapping(target = "groupNames", source = "groups", qualifiedByName = "mapGroupsToGroupNames")
	LecturerDataResponse lecturerToLecturerDataResponse(LecturerEntity lecturer);

	@Named("mapGroupsToGroupNames")
	default Set<String> mapGroupsToGroupNames(Set<GroupEntity> groups) {
		return groups.stream()
				.map(GroupEntity::getName) 
				.collect(Collectors.toSet());
	}
}
