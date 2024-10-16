package edutrack.lecturer.util;

import edutrack.group.entity.GroupEntity;
import edutrack.lecturer.dto.request.LecturerCreateRequest;
import edutrack.lecturer.dto.request.LecturerUpdateRequest;
import edutrack.lecturer.dto.response.LecturerDataResponse;
import edutrack.lecturer.entity.LecturerEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings("checkstyle:RegexpMultiline")
@Mapper(componentModel = "spring")
public interface EntityDtoLecturerMapper {

    EntityDtoLecturerMapper INSTANCE = Mappers.getMapper(EntityDtoLecturerMapper.class);

    LecturerEntity toLecturerEntity(LecturerCreateRequest request, Set<GroupEntity> groups);

    default void updateLecturerFromRequest(LecturerUpdateRequest request, @MappingTarget LecturerEntity lecturer) {
        if (request.getFirstName() != null) {
            lecturer.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            lecturer.setLastName(request.getLastName());
        }
        if (request.getPhoneNumber() != null) {
            lecturer.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getEmail() != null) {
            lecturer.setEmail(request.getEmail());
        }
        if (request.getCity() != null) {
            lecturer.setCity(request.getCity());
        }
        if (request.getStatus() != null) {
            lecturer.setStatus(request.getStatus());
        }
    }

    @Mapping(target = "groupIds", source = "groups", qualifiedByName = "mapGroupEntitiesToIds")
    LecturerDataResponse toLecturerDataResponse(LecturerEntity lecturer);

    @Named("mapGroupEntitiesToIds")
    default Set<Long> mapGroupEntitiesToIds(Set<GroupEntity> groups) {
        return groups.stream()
                .map(GroupEntity::getId)
                .collect(Collectors.toSet());
    }
}