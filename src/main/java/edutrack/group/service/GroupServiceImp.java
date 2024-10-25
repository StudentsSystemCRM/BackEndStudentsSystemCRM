package edutrack.group.service;

import edutrack.group.constant.GroupStatus;
import edutrack.group.dto.request.GroupCreateRequest;
import edutrack.group.dto.request.GroupUpdateDataRequest;
import edutrack.group.dto.response.GroupDataResponse;
import edutrack.group.entity.GroupEntity;
import edutrack.group.exception.GroupNotFoundException;
import edutrack.group.repository.GroupRepository;
import edutrack.group.util.EntityDtoGroupMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class GroupServiceImp implements GroupService {

	GroupRepository groupRepo;

    private GroupEntity findGroupById(Long id) {
        return groupRepo.findById(id).orElseThrow(() -> new GroupNotFoundException("Group with id " + id + " not found"));
    }
    
    @Override
    public GroupDataResponse getGroupById(Long id) {
        return EntityDtoGroupMapper.INSTANCE.groupToGroupDataResponse(findGroupById(id));
    }

	@Override
	@Transactional
	public GroupDataResponse createGroup(GroupCreateRequest groupRequest) {
		GroupEntity groupResponse = groupRepo.findByName(groupRequest.getName());
		if (groupResponse != null) {
			throw new ResponseStatusException(HttpStatus.CONFLICT,
					"Group with name " + groupRequest.getName() + " is already exists");
		}
		GroupEntity groupEntity = EntityDtoGroupMapper.INSTANCE.groupCreateRequestToGroup(groupRequest);
		groupEntity.setStatus(GroupStatus.ACTIVE);
		groupRepo.save(groupEntity);
		return EntityDtoGroupMapper.INSTANCE.groupToGroupDataResponse(groupEntity);
	}

	@Override
	@Transactional
	public GroupDataResponse updateGroup(GroupUpdateDataRequest groupRequest) {
		GroupEntity groupEntity = findGroupById(groupRequest.getId());
		if (groupEntity.getName() != null) {
			groupEntity.setName(groupRequest.getName());
		}
		if (groupEntity.getWhatsApp() != null) {
			groupEntity.setWhatsApp(groupRequest.getWhatsApp());
		}
		if (groupEntity.getSkype() != null) {
			groupEntity.setSkype(groupRequest.getSkype());
		}
		if (groupEntity.getSlack() != null) {
			groupEntity.setSlack(groupRequest.getSlack());
		}
		if (groupEntity.getStatus() != null) {
			groupEntity.setStatus(groupRequest.getStatus());
		}
		if (groupEntity.getStartDate() != null) {
			groupEntity.setStartDate(groupRequest.getStartDate());
		}
		if (groupEntity.getExpFinishDate() != null) {
			groupEntity.setExpFinishDate(groupRequest.getExpFinishDate());
		}
		if (groupEntity.getLessonsDays() != null) {
			groupEntity.setLessonsDays(groupRequest.getLessonsDays());
		}
		if (groupEntity.getWebinarsDays() != null) {
			groupEntity.setWebinarsDays(groupRequest.getWebinarsDays());
		}
		if (groupRequest.getDeactivateAfter30Days() != null) {
			groupEntity.setDeactivateAfter30Days(groupRequest.getDeactivateAfter30Days());
		}
		groupEntity.setLastModifiedDate(ZonedDateTime.now());
		groupRepo.save(groupEntity);
		return EntityDtoGroupMapper.INSTANCE.groupToGroupDataResponse(groupEntity);
	}

	public List<GroupDataResponse> getAllGroups() {
		List<GroupEntity> groupResponse = groupRepo.findAll();
        return (groupResponse.isEmpty() || groupResponse == null) ? new ArrayList<>() :  groupResponse.stream().map(EntityDtoGroupMapper.INSTANCE::groupToGroupDataResponse).collect(Collectors.toList());
	}
	
	@Override
	public List<GroupDataResponse> getAllGroups(Pageable pageable) {
		Page<GroupEntity> groupResponse = groupRepo.findAll(pageable);
		return (groupResponse.isEmpty() || groupResponse == null) ? new ArrayList<>() : groupResponse.stream().map(EntityDtoGroupMapper.INSTANCE::groupToGroupDataResponse).collect(Collectors.toList());
	}
    
	@Override
	public List<GroupDataResponse> getGroupsByStatus(GroupStatus status) {
		List<GroupEntity> groupResponse = groupRepo.findByStatus(status);
		return (groupResponse.isEmpty() || groupResponse == null) ? new ArrayList<>() : groupResponse.stream().map(EntityDtoGroupMapper.INSTANCE::groupToGroupDataResponse).collect(Collectors.toList());
	}

    @Override
	public List<GroupDataResponse> getGroupsByName(String name) {
		List<GroupEntity> groupResponse = groupRepo.findByNameContainingIgnoreCase(name);
		if (groupResponse == null) {
			throw new GroupNotFoundException("The group that contains " + name + " in the name not found");
		}
		return groupResponse.stream().map(EntityDtoGroupMapper.INSTANCE::groupToGroupDataResponse).collect(Collectors.toList());
	}
    
	@Override
	@Transactional
	public List<Long> getStudentsIdsByGroup(Long id) {
//		List<Long> groupResponse = groupRepo.findStudentsIdsByGroup(id);
//      return (groupResponse.isEmpty() || groupResponse == null) ? new ArrayList<>() : groupResponse;
		GroupEntity groupResponse = findGroupById(id);
		return (groupResponse == null) ? new ArrayList<>() : groupResponse.getStudents().stream().map(student -> student.getId()).collect(Collectors.toList());
	}
	
	@Override
	public List<GroupDataResponse> getGroupsByGroupsIds(List<Long> ids) {
		List<GroupEntity> groupResponse = groupRepo.findAllById(ids);
		return (groupResponse.isEmpty() || groupResponse == null) ? new ArrayList<>() : groupResponse.stream().map(EntityDtoGroupMapper.INSTANCE::groupToGroupDataResponse).collect(Collectors.toList());
	}

	@Override
	@Transactional
	public Boolean deleteGroup(Long id) {
		GroupEntity group = findGroupById(id);
		group.getStudents().forEach(student -> group.getStudents().remove(student));
		group.getStudents().clear();
		groupRepo.deleteById(group.getId());
		return true;
	}
	
}
