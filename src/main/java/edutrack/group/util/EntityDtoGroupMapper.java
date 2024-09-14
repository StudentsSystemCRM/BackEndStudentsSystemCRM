package edutrack.group.util;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import edutrack.group.dto.request.GroupCreateRequest;
import edutrack.group.entity.GroupEntity;

@Mapper
public interface EntityDtoGroupMapper {
	
	EntityDtoGroupMapper INSTANCE = Mappers.getMapper(EntityDtoGroupMapper.class);
	
    @Mapping(target = "groupReminders", ignore = true)
    @Mapping(target = "deactivateAfter30Days", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    GroupEntity groupCreateRequestToGroup(GroupCreateRequest groupCreate);

}
