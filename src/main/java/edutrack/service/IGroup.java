package edutrack.service;


import edutrack.dto.request.students.GroupCreateRequest;
import edutrack.dto.request.students.GroupUpdateDataRequest;
import edutrack.dto.response.students.GroupDataResponse;

public interface IGroup {
	
	GroupDataResponse createGroup(GroupCreateRequest group);
	GroupDataResponse updateGroup(GroupUpdateDataRequest group);

}
