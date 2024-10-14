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

@Mapper(componentModel = "spring")
public interface EntityDtoLecturerMapper {
    EntityDtoLecturerMapper INSTANCE = Mappers.getMapper(EntityDtoLecturerMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    LecturerEntity toLecturerEntity(LecturerCreateRequest request, Set<GroupEntity> groups);
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    void updateLecturerFromRequest(LecturerUpdateRequest request, @MappingTarget LecturerEntity lecturer);

    @Mapping(target = "groupIds", source = "groups", qualifiedByName = "mapGroupEntitiesToIds")
    LecturerDataResponse toLecturerDataResponse(LecturerEntity lecturer);
    @Named("mapGroupEntitiesToIds")
    default Set<Long> mapGroupEntitiesToIds(Set<GroupEntity> groups) {
        return groups.stream()
                .map(GroupEntity::getId)
                .collect(Collectors.toSet());
    }
}






