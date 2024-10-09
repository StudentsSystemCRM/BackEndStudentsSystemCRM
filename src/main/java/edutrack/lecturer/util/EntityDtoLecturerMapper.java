package edutrack.lecturer.util;

import edutrack.lecturer.dto.request.LecturerCreateRequest;
import edutrack.lecturer.dto.request.LecturerUpdateRequest;
import edutrack.lecturer.entity.LecturerEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface EntityDtoLecturerMapper {
	EntityDtoLecturerMapper INSTANCE = Mappers.getMapper(EntityDtoLecturerMapper.class);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "lastModifiedBy", ignore = true)
	LecturerEntity toLecturerEntity(LecturerCreateRequest request);

	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "lastModifiedBy", ignore = true)
	void updateLecturerFromRequest(LecturerUpdateRequest request, @MappingTarget LecturerEntity lecturer);
}


