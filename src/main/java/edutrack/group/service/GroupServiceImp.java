package edutrack.group.service;

import edutrack.exception.StudentNotFoundException;
import edutrack.group.constant.GroupStatus;
import edutrack.group.dto.request.GroupCreateRequest;
import edutrack.group.dto.request.GroupUpdateDataRequest;
import edutrack.group.dto.response.GroupDataResponse;
import edutrack.group.entity.GroupEntity;
import edutrack.group.repository.GroupRepository;
import edutrack.group.util.EntityDtoGroupMapper;
import edutrack.student.entity.StudentEntity;
import edutrack.student.repository.StudentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class GroupServiceImp implements GroupService {

    GroupRepository groupRepo;
    StudentRepository studentRepo;
	
	private GroupDataResponse toGroupDataResponse(GroupEntity group) {
        Boolean deactivateAfter30Days = false;
        if (group.getDeactivateAfter30Days()!=null) deactivateAfter30Days = true;
		return new GroupDataResponse(group.getName(), group.getWhatsApp(), group.getSkype(), group.getSlack(),group.getStatus(),
        		group.getStartDate(), group.getExpFinishDate(), group.getLessonsDays(), group.getWebinarsDays(), deactivateAfter30Days, 
        		group.getStudents(),group.getGroupReminders());
	}

	@Override
	@Transactional
	public GroupDataResponse createGroup(GroupCreateRequest groupRequest) {
        GroupEntity groupResponse = groupRepo.findByName(groupRequest.getName());
        if (groupResponse != null)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Group with name " + groupRequest.getName() + " is already exists");
        GroupEntity groupEntity = EntityDtoGroupMapper.INSTANCE.groupCreateRequestToGroup(groupRequest);
        groupRepo.save(groupEntity);
        return toGroupDataResponse(groupEntity);
	}

	@Override
	public GroupDataResponse updateGroup(GroupUpdateDataRequest group) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<GroupDataResponse> getAllGroups() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<GroupDataResponse> getGroupsByStatus(GroupStatus status) {
		// TODO Auto-generated method stub
		return null;
	}

	private GroupEntity findGroupByName(String name) {
		return groupRepo.findByName(name);
	}

    private StudentEntity findStudentById(Long id) {
        return studentRepo.findById(id).orElseThrow(
                () -> new StudentNotFoundException("Student with id " + id + " not found"));
    }

	@Override
	public GroupDataResponse deleteGroup(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Transactional
	public GroupDataResponse addStudentToGroup(Long id, String name) {
		StudentEntity student = findStudentById(id);
		GroupEntity group = groupRepo.findByName(name);
		List<StudentEntity> students = group.getStudents();
		if (students==null) {
			students = new ArrayList<>();
		}
		students.add(student);
		group.setStudents(students);
		groupRepo.save(group);
		return toGroupDataResponse(group);
	}

	@Override
	public GroupDataResponse getGroupByName(String name) {
		return toGroupDataResponse(findGroupByName(name));
	}

	@Override
	public List<GroupDataResponse> getStudentGroups(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GroupDataResponse deleteStudentFromGroup(Long id, String name) {
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

