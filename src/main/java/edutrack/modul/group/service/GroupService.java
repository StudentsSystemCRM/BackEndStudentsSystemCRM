package edutrack.modul.group.service;

import java.util.List;

import edutrack.constant.GroupStatus;
import edutrack.modul.group.dto.request.GroupCreateRequest;
import edutrack.modul.group.dto.request.GroupUpdateDataRequest;
import edutrack.modul.group.dto.response.GroupDataResponse;

public interface GroupService {
	
	GroupDataResponse createGroup(GroupCreateRequest group);
	
	List<GroupDataResponse> getAllGroups();
	List<GroupDataResponse> getGroupsByStatus(GroupStatus status);
	GroupDataResponse getGroupByName(String name);
	List<GroupDataResponse> getStudentGroups(Long id);
	
	GroupDataResponse addStudentToGroup(Long id, String name);
	
	GroupDataResponse updateGroup(GroupUpdateDataRequest group);
	
	GroupDataResponse deleteGroup(String name);

}
