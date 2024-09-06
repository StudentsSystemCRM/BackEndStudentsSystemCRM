package edutrack.modul.group.service;


import edutrack.modul.group.dto.request.GroupCreateRequest;
import edutrack.modul.group.dto.request.GroupUpdateDataRequest;
import edutrack.modul.group.dto.response.GroupDataResponse;

public interface GroupService {
	
	GroupDataResponse createGroup(GroupCreateRequest group);
	GroupDataResponse updateGroup(GroupUpdateDataRequest group);

}
