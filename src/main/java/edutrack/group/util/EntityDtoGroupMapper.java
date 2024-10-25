package edutrack.group.util;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import edutrack.group.constant.GroupStatus;
import edutrack.group.dto.request.GroupCreateRequest;
import edutrack.group.dto.response.GroupDataResponse;
import edutrack.group.entity.GroupEntity;
import java.time.ZonedDateTime;

@Mapper(imports = {ZonedDateTime.class, GroupStatus.class})
public interface EntityDtoGroupMapper {
	
	EntityDtoGroupMapper INSTANCE = Mappers.getMapper(EntityDtoGroupMapper.class);
	
	@Mapping(target = "id", ignore = true)
    @Mapping(target = "groupSchedulers", ignore = true)
    @Mapping(target = "deactivateAfter30Days", expression = "java(false)")
    @Mapping(target = "status", expression = "java(GroupStatus.ACTIVE)")
	@Mapping(target = "students", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "createdDate", expression = "java(ZonedDateTime.now())")
    @Mapping(target = "lastModifiedDate", ignore = true)
    GroupEntity groupCreateRequestToGroup(GroupCreateRequest groupCreate);

	GroupDataResponse groupToGroupDataResponse(GroupEntity group);
    
}
