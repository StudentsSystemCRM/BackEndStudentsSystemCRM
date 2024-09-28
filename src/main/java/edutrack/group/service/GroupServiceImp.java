package edutrack.group.service;

import edutrack.exception.StudentNotFoundException;
import edutrack.group.constant.GroupStatus;
import edutrack.group.constant.WeekDay;
import edutrack.group.dto.request.GroupCreateRequest;
import edutrack.group.dto.request.GroupUpdateDataRequest;
import edutrack.group.dto.response.GroupDataResponse;
import edutrack.group.entity.GroupEntity;
import edutrack.group.exception.GroupNotFoundException;
import edutrack.group.repository.GroupRepository;
import edutrack.group.util.EntityDtoGroupMapper;
import edutrack.student.entity.StudentEntity;
import edutrack.student.repository.StudentRepository;
import edutrack.user.exception.ResourceExistsException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
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
        		group.getStartDate(), group.getExpFinishDate()
        		,getLessonsDays(group.getName()), getWebinarsDays(group.getName())
        		,deactivateAfter30Days
//        		,group.getStudents(),group.getGroupReminders()
        		);
	}

    private StudentEntity findStudentById(Long id) {
        return studentRepo.findById(id).orElseThrow(
                () -> new StudentNotFoundException("Student with id " + id + " not found"));
    }
    
    private List<WeekDay> getLessonsDays(String name) {
        return groupRepo.getLessonsDays(name);
    }
	
    private List<WeekDay> getWebinarsDays(String name) {
        return groupRepo.getWebinarsDays(name);
    }
    
	private GroupEntity findGroupByName(String name) {
		GroupEntity groupEntity = groupRepo.findByName(name);
        if (groupEntity == null)
            throw new GroupNotFoundException("Group with name " + name + "  not found");
        return groupEntity;
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
	
//	@Override
//	public List<GroupDataResponse> getAllGroups() {
//		List<GroupEntity> groupResponse = groupRepo.findAll();
//        if (groupResponse.isEmpty())
//            return new ArrayList<>();
//        return groupResponse.stream().map(group -> toGroupDataResponse(group)).collect(Collectors.toList());
//	}
	
	@Override
	public List<GroupDataResponse> getAllGroups(Pageable pageable) {
        return groupRepo.findAll(pageable).isEmpty() ? new ArrayList<>() : groupRepo.findAll(pageable).stream().map(group -> toGroupDataResponse(group)).collect(Collectors.toList());
	}

	@Override
	public List<GroupDataResponse> getGroupsByStatus(GroupStatus status) {
        List<GroupEntity> groupResponse = groupRepo.findByStatus(status);
        if (groupResponse == null || groupResponse.isEmpty())
            return new ArrayList<>();
        return groupResponse.stream().map(group -> toGroupDataResponse(group)).collect(Collectors.toList());
	}

	@Override
	@Transactional
	public GroupDataResponse addStudentToGroup(Long id, String name) {
		StudentEntity student = findStudentById(id);
		GroupEntity group = findGroupByName(name);
		student.setOriginalGroup(group.getName());
		studentRepo.save(student);
		List<StudentEntity> students = group.getStudents();
		if (students==null) {
			students = new ArrayList<>();
		}
		if (students.contains(student)) {
			throw new ResourceExistsException("Student with id " + id + " already exists in group " + name);
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
	@Transactional
	public List<GroupDataResponse> getStudentGroups(Long id) {
		 StudentEntity student = findStudentById(id);
		 List<GroupEntity> groupResponse = student.getGroups();
		 return groupResponse.stream().map(group -> toGroupDataResponse(group)).collect(Collectors.toList());
	}

	@Override
	@Transactional
	public Boolean deleteStudentFromGroup(Long id, String name) {
		StudentEntity student = findStudentById(id);
        GroupEntity group = findGroupByName(name);
        if (!group.getStudents().remove(student)) throw new StudentNotFoundException("Student with id " + id + " not found in group " + name);
        groupRepo.save(group);
		student.setOriginalGroup(null);
		studentRepo.save(student);
        return true;
	}
	
	@Override
	@Transactional
	public GroupDataResponse updateGroup(GroupUpdateDataRequest groupRequest) {
        GroupEntity groupEntity = groupRepo.findByName(groupRequest.getName());
        if (groupEntity == null)
            throw new GroupNotFoundException("Group with name " + groupRequest.getName() + "  doesn't exists");
        groupEntity.setName(groupRequest.getName());
        if(groupEntity.getWhatsApp() != null)
        	groupEntity.setWhatsApp(groupRequest.getWhatsApp());
        if(groupEntity.getSkype() != null)
       	 	groupEntity.setSkype(groupRequest.getSkype());
        if(groupEntity.getSlack() != null)
       	 	groupEntity.setSlack(groupRequest.getSlack());
        if(groupEntity.getStatus() != null)
       	 	groupEntity.setStatus(groupRequest.getStatus());
        if(groupEntity.getStartDate() != null)
       	 	groupEntity.setStartDate(groupRequest.getStartDate());
        if(groupEntity.getExpFinishDate() != null)
       	 	groupEntity.setExpFinishDate(groupRequest.getExpFinishDate());
        if(groupEntity.getLessonsDays() != null)
       	 	groupEntity.setLessonsDays(groupRequest.getLessonsDays());
        if(groupEntity.getWebinarsDays() != null)
       	 	groupEntity.setWebinarsDays(groupRequest.getWebinarsDays());
        if (groupRequest.getDeactivateAfter30Days() != null)
        	groupEntity.setDeactivateAfter30Days(groupRequest.getExpFinishDate().plusDays(30));
        groupRepo.save(groupEntity);
        return toGroupDataResponse(groupEntity);
	}

	@Override
	@Transactional
	public GroupDataResponse deleteGroup(String name) {
        GroupEntity group = findGroupByName(name);
        group.getStudents().forEach(student -> group.getStudents().remove(student));
        group.getStudents().clear();
        groupRepo.deleteById(name);
        return toGroupDataResponse(group);
	}

	@Override
	@Transactional
	public Boolean changeStudentGroup(Long id, String groupName, String oldGroupName) {
		StudentEntity student = findStudentById(id);
        GroupEntity oldGroup = groupRepo.findByName(oldGroupName);
        if (oldGroup == null)
            throw new GroupNotFoundException("Group with name " + oldGroupName + " doesn't exists");
        if (!oldGroup.getStudents().contains(student)) throw new StudentNotFoundException("Student with id " + id + " not found in group " + oldGroupName);
        GroupEntity group = groupRepo.findByName(groupName);
        if (group == null)
            throw new GroupNotFoundException("Group with name " + groupName + " doesn't exists");    
        groupRepo.updateStudentGroups(id, groupName, oldGroupName);
		student.setOriginalGroup(groupName);
		studentRepo.save(student);
		return true;
//		deleteStudentFromGroup(id, oldGroupName);
//		addStudentToGroup(id, groupName);
	}

}

