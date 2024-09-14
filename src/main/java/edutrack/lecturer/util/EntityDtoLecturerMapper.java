package edutrack.lecturer.util;

import edutrack.lecturer.dto.request.LecturerCreateRequest;
import edutrack.lecturer.dto.response.LecturerDataResponse;
import edutrack.lecturer.entity.LecturerEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface EntityDtoLecturerMapper {
	EntityDtoLecturerMapper INSTANCE = Mappers.getMapper(EntityDtoLecturerMapper.class);

	LecturerEntity lecturerCreateRequestToLecturer(LecturerCreateRequest lecturerCreateRequest);

	LecturerDataResponse lecturerToLecturerDataResponse(LecturerEntity lecturerEntity);
}
