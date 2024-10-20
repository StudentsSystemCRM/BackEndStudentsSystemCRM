package edutrack.student.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import edutrack.student.dto.request.StudentCreateRequest;
import edutrack.student.dto.request.StudentUpdateDataRequest;
import edutrack.student.dto.response.StudentDataResponse;
import edutrack.student.util.EntityDtoStudentMapper;
import edutrack.user.exception.ResourceExistsException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import edutrack.exception.StudentNotFoundException;
import edutrack.activityLog.entity.ActivityLogEntity;
import edutrack.activityLog.repository.ActivityLogRepository;
import edutrack.group.exception.GroupNotFoundException;
import edutrack.payment.repository.PaymentRepository;
import edutrack.student.entity.StudentEntity;
import edutrack.student.exception.EmailAlreadyInUseException;
import edutrack.student.repository.StudentRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class StudentServiceImp implements StudentService {
    StudentRepository studentRepo;
    ActivityLogRepository activityRepo;
    PaymentRepository paymentRepo;

    private ActivityLogEntity createActivityLogByStudent(StudentEntity studentEntity, String comment) {
        ActivityLogEntity log = new ActivityLogEntity(null, LocalDate.now(), comment, studentEntity);
        return activityRepo.save(log);
    }	

    private StudentEntity findStudentById(Long id) {
        return studentRepo.findById(id).orElseThrow(
                () -> new StudentNotFoundException("Student with id " + id + " not found"));
    }
    
    @Override
    @Transactional
    public StudentDataResponse createStudent(StudentCreateRequest student) {
        if (studentRepo.findByEmail(student.getEmail()) != null) {
            throw new EmailAlreadyInUseException("Student with email " + student.getEmail() + " already exists.");
        }

        StudentEntity studentEntity = EntityDtoStudentMapper.INSTANCE.studentCreateRequestToStudent(student);
        studentEntity = studentRepo.save(studentEntity);
        String comment = student.getComment() == null ? "Create student data" : student.getComment();
        createActivityLogByStudent(studentEntity, comment);
        return EntityDtoStudentMapper.INSTANCE.studentToStudentDataResponse(studentEntity);
    }

    @Override
    @Transactional
    public StudentDataResponse updateStudent(StudentUpdateDataRequest student) {
        StudentEntity studentEntity = findStudentById(student.getId());

        if (!student.getEmail().equals(studentEntity.getEmail())) {
            StudentEntity existingStudent = studentRepo.findByEmail(student.getEmail());
            if (existingStudent != null) {
            	throw new EmailAlreadyInUseException("Email " + student.getEmail() + " is already in use by another student.");
            }
        }

        studentEntity.setFirstName(student.getName());
        studentEntity.setLastName(student.getSurname());
        studentEntity.setPhoneNumber(student.getPhone());
        studentEntity.setEmail(student.getEmail());
        if (student.getCity() != null) {
        	studentEntity.setCity(student.getCity());
        }
        if (student.getCourse() != null) {
        	studentEntity.setCourse(student.getCourse());
        }
        if (student.getSource() != null) {
        	studentEntity.setSource(student.getSource());
        }
		if (student.getLeadStatus() != null) {
			studentEntity.setLeadStatus(student.getLeadStatus());
		}
		studentEntity.setLastModifiedDate(LocalDateTime.now());
        studentEntity = studentRepo.save(studentEntity);
        return EntityDtoStudentMapper.INSTANCE.studentToStudentDataResponse(studentEntity);
    }
    
    @Override
    public StudentDataResponse getStudentById(Long id) {
        return EntityDtoStudentMapper.INSTANCE.studentToStudentDataResponse(findStudentById(id));
    }
    
    @Override
    public List<StudentDataResponse> getAllStudents(Pageable pageable) {
    	Page<StudentEntity> studentResponse = studentRepo.findAll(pageable);
        return (studentResponse == null || studentResponse.isEmpty()) ? new ArrayList<>() : studentResponse.stream().map(EntityDtoStudentMapper.INSTANCE::studentToStudentDataResponse).collect(Collectors.toList());
    }

    @Override
    public List<StudentDataResponse> getStudentsByName(Pageable pageable, String name) {
        List<StudentEntity> studentResponse = studentRepo.findByFirstNameContainingIgnoreCase(pageable, name);
        return (studentResponse == null || studentResponse.isEmpty()) ? new ArrayList<>() : studentResponse.stream().map(EntityDtoStudentMapper.INSTANCE::studentToStudentDataResponse).collect(Collectors.toList());
    }

    @Override
    public List<StudentDataResponse> getStudentsBySurname(Pageable pageable, String surname) {
        List<StudentEntity> studentResponse = studentRepo.findByLastNameContainingIgnoreCase(pageable, surname);
        return (studentResponse == null || studentResponse.isEmpty()) ? new ArrayList<>() : studentResponse.stream().map(EntityDtoStudentMapper.INSTANCE::studentToStudentDataResponse).collect(Collectors.toList());
    }

    @Override
    public List<StudentDataResponse> getStudentsByNameAndSurname(Pageable pageable, String name, String surname) {
        List<StudentEntity> studentResponse = studentRepo.findByFirstNameContainingIgnoreCaseAndLastNameContainingIgnoreCase(pageable, name, surname);
        return (studentResponse == null || studentResponse.isEmpty()) ? new ArrayList<>() : studentResponse.stream().map(EntityDtoStudentMapper.INSTANCE::studentToStudentDataResponse).collect(Collectors.toList());
    }

	@Override
	public List<StudentDataResponse> getStudentsByStudentsIds(List<Long> ids) {
		List<StudentEntity> studentResponse = studentRepo.findAllById(ids);
		return (studentResponse == null || studentResponse.isEmpty()) ? new ArrayList<>() : studentResponse.stream().map(EntityDtoStudentMapper.INSTANCE::studentToStudentDataResponse).collect(Collectors.toList());
	}

    @Override
    @Transactional
    public Boolean deleteStudent(Long id) {
        StudentEntity student = findStudentById(id);
        student.getGroups().forEach(group -> group.getStudents().remove(student));
        student.getGroups().clear();
        activityRepo.deleteByStudentId(id);
        paymentRepo.deleteByStudentId(id);
        studentRepo.deleteById(id);
        return true;
    }
	
	@Override
	@Transactional
	public Boolean addStudentToGroup(Long studentId, Long groupId) {
		StudentEntity student = findStudentById(studentId);
		student.setOriginalGroupId(groupId);
		studentRepo.save(student);
		List<Long> ids = getStudentGroupsIds(studentId);
		if (ids != null && ids.contains(studentId)) {
			throw new ResourceExistsException("Student with id " + studentId + " already exists in group with id " + groupId);
		}
		studentRepo.addStudentToGroup(studentId, groupId);
		return true;
	}
	
	@Override
	@Transactional
	public Boolean deleteStudentFromGroup(Long studentId, Long groupId) {
		if (studentRepo.groupExistsById(groupId) == null) {
			throw new GroupNotFoundException("Group with id " + groupId + " doesn't exists");
		}
		List<Long> ids = getStudentGroupsIds(studentId);
		if (ids == null || !ids.contains(groupId)) {
			throw new StudentNotFoundException("Student with id " + studentId + " not found in group " + groupId);
		}
		StudentEntity student = findStudentById(studentId);
		if (student.getOriginalGroupId() == groupId) {
			student.setOriginalGroupId(null);
		}
		studentRepo.save(student);
		studentRepo.deleteStudentFromGroup(studentId, groupId);
		return true;
	}
	
	@Override
	public List<Long> getStudentGroupsIds(Long id) {
		List<Long> studentResponse = studentRepo.findStudentGroupsIds(id);
		return (studentResponse.isEmpty() || studentResponse == null) ? new ArrayList<>() : studentResponse;
	}
	
	@Override
	@Transactional
	public Boolean changeStudentGroup(Long studentId, Long groupId, Long oldGroupId) {
		StudentEntity student = findStudentById(studentId);
		List<Long> ids = getStudentGroupsIds(studentId);
		if (ids == null || !ids.contains(groupId)) {
			throw new StudentNotFoundException("Student with id " + studentId + " not found in group " + groupId);
		}
		if (studentRepo.groupExistsById(groupId) == null) {
			throw new GroupNotFoundException("Group with id " + groupId + " doesn't exists");
		}
		if (studentRepo.groupExistsById(oldGroupId) == null) {
			throw new GroupNotFoundException("Group with id " + oldGroupId + " doesn't exists");
		}
		student.setOriginalGroupId(groupId);
		studentRepo.save(student);
		studentRepo.updateStudentGroups(studentId, groupId, oldGroupId);
		return true;
	}
}

