package edutrack.service;


import edutrack.dto.request.group.GroupCreateRequest;
import edutrack.dto.request.group.GroupUpdateDataRequest;
import edutrack.dto.response.group.GroupDataResponse;

public interface IGroup {
	
	GroupDataResponse createGroup(GroupCreateRequest group);
	GroupDataResponse updateGroup(GroupUpdateDataRequest group);

}
