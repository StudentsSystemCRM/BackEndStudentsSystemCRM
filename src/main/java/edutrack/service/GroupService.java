package edutrack.service;

import edutrack.dto.request.group.GroupCreateRequest;
import edutrack.dto.request.group.GroupUpdateDataRequest;
import edutrack.dto.response.group.GroupDataResponse;
import edutrack.entity.students.Group;
import edutrack.repository.GroupRepository;
import edutrack.util.EntityDtoMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class GroupService implements IGroup {

    GroupRepository groupRepo;

	
	private GroupDataResponse toGroupDataResponse(Group group) {
        Boolean deactivateAfter30Days = false;
        if (group.getDeactivateAfter30Days()!=null) deactivateAfter30Days = true;
		return new GroupDataResponse(group.getName(), group.getWhatsApp(), group.getSkype(), group.getSlack(),group.getStatus(),
        		group.getStartDate(), group.getExpFinishDate(), group.getLessonsDays(), group.getWebinarsDays(), deactivateAfter30Days, 
        		group.getStudents(),group.getGroupReminders());
	}

	@Override
	@Transactional
	public GroupDataResponse createGroup(GroupCreateRequest groupRequest) {
        Group groupResponse = groupRepo.findByName(groupRequest.getName());
        if (groupResponse != null)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Group with name " + groupRequest.getName() + " is already exists");
        Group groupEntity = EntityDtoMapper.INSTANCE.groupCreateRequestToGroup(groupRequest);
        groupRepo.save(groupEntity);
        return toGroupDataResponse(groupEntity);
	}

	@Override
	public GroupDataResponse updateGroup(GroupUpdateDataRequest group) {
		// TODO Auto-generated method stub
		return null;
	}


//	@Override
//	@Transactional
//	public GroupDataResponse updateGroup(GroupUpdateDataRequest groupRequest) {
//        Group groupEntity = groupRepo.findByName(groupRequest.getName());
//        if (groupEntity == null)
//            throw new ResponseStatusException(HttpStatus.CONFLICT, "Group with name " + groupRequest.getName() + "  doesn't exists");
//        groupEntity.setName(groupRequest.getName());
//        if(groupEntity.getWhatsApp() != null)
//        	groupEntity.setWhatsApp(groupRequest.getWhatsApp());
//        if(groupEntity.getSkype() != null)
//       	 	groupEntity.setSkype(groupRequest.getSkype());
//        if(groupEntity.getSlack() != null)
//       	 	groupEntity.setSlack(groupRequest.getSlack());
//        if(groupEntity.getStatus() != null)
//       	 	groupEntity.setStatus(groupRequest.getStatus());
//        if(groupEntity.getStartDate() != null)
//       	 	groupEntity.setStartDate(groupRequest.getStartDate());
//        if(groupEntity.getExpFinishDate() != null)
//       	 	groupEntity.setExpFinishDate(groupRequest.getExpFinishDate());
//        if(groupEntity.getLessonsDays() != null)
//       	 	groupEntity.setLessonsDays(groupRequest.getLessonsDays());
//        if(groupEntity.getWebinarsDays() != null)
//       	 	groupEntity.setWebinarsDays(groupRequest.getWebinarsDays());
//        if (groupRequest.getDeactivateAfter30Days() != null)
//        	groupEntity.setDeactivateAfter30Days(groupRequest.getExpFinishDate().plusDays(30));
//        if (groupRequest.getStudents() != null)
//        	groupEntity.setStudents(groupRequest.getStudents());
//        groupRepo.save(groupEntity);
//        return toGroupDataResponse(groupEntity);
//	}

}

