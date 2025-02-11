package edutrack.group.service;

import java.time.DayOfWeek;
import java.util.List;

import org.springframework.data.domain.Pageable;

import edutrack.group.dto.response.GroupDataResponse;
import edutrack.group.constant.GroupStatus;
import edutrack.group.dto.request.GroupCreateRequest;
import edutrack.group.dto.request.GroupUpdateDataRequest;

public interface GroupService {
	
	GroupDataResponse createGroup(GroupCreateRequest group);
	GroupDataResponse updateGroup(GroupUpdateDataRequest group);
	GroupDataResponse getGroupById(Long id);
	
	List<GroupDataResponse> getAllGroups(Pageable pageable);
	List<GroupDataResponse> getGroupsByStatus(GroupStatus status);
	List<GroupDataResponse> getGroupsByName(String name);// containing, ignore case
	List<GroupDataResponse> getGroupsByGroupsIds(List<Long> ids);
	List<GroupDataResponse> getGroupsByLessonsDate(DayOfWeek dayOfWeek);
	List<GroupDataResponse> getGroupsByWebinarsDate(DayOfWeek dayOfWeek);
	List<Long> getStudentsIdsByGroup(Long id);
	
	Boolean deleteGroup(Long id);

}
