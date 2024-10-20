package edutrack.group.util;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import edutrack.group.constant.GroupStatus;
import edutrack.group.dto.request.GroupCreateRequest;
import edutrack.group.dto.response.GroupDataResponse;
import edutrack.group.entity.GroupEntity;
import java.time.LocalDateTime;

@Mapper(imports = {LocalDateTime.class, GroupStatus.class})
public interface EntityDtoGroupMapper {
	
	EntityDtoGroupMapper INSTANCE = Mappers.getMapper(EntityDtoGroupMapper.class);
	
	@Mapping(target = "id", ignore = true)
    @Mapping(target = "groupShedulers", ignore = true)
    @Mapping(target = "deactivateAfter30Days", expression = "java(false)")
    @Mapping(target = "status", expression = "java(GroupStatus.ACTIVE)")
	@Mapping(target = "students", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "createdDate", expression = "java(LocalDateTime.now())")
    @Mapping(target = "lastModifiedDate", ignore = true)
    GroupEntity groupCreateRequestToGroup(GroupCreateRequest groupCreate);

	GroupDataResponse groupToGroupDataResponse(GroupEntity group);
    
    
}
