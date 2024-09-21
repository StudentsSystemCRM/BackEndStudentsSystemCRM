package edutrack.group.service;

import java.util.List;

import org.springframework.data.domain.Pageable;

import edutrack.group.dto.response.GroupDataResponse;
import edutrack.group.constant.GroupStatus;
import edutrack.group.dto.request.GroupCreateRequest;
import edutrack.group.dto.request.GroupUpdateDataRequest;

public interface GroupService {
	
	GroupDataResponse createGroup(GroupCreateRequest group);
	
	List<GroupDataResponse> getAllGroups();
	List<GroupDataResponse> getAllGroups(Pageable pageable);
	List<GroupDataResponse> getGroupsByStatus(GroupStatus status);
	GroupDataResponse getGroupByName(String name);
	List<GroupDataResponse> getStudentGroups(Long id);
	
	GroupDataResponse addStudentToGroup(Long id, String name);
	GroupDataResponse deleteStudentFromGroup(Long id, String name);
	
	GroupDataResponse updateGroup(GroupUpdateDataRequest group);
	
	GroupDataResponse deleteGroup(String name);

}
