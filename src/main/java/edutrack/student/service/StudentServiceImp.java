package edutrack.student.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import edutrack.student.dto.request.StudentCreateRequest;
import edutrack.student.dto.request.StudentUpdateDataRequest;
import edutrack.student.dto.response.StudentDataResponse;
import edutrack.student.util.EntityDtoStudentMapper;
import org.springframework.stereotype.Service;

import edutrack.exception.StudentNotFoundException;
import edutrack.activityLog.entity.ActivityLogEntity;
import edutrack.activityLog.repository.ActivityLogRepository;
import edutrack.group.entity.GroupEntity;
import edutrack.group.repository.GroupRepository;
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
    GroupRepository groupRepo;

    @Override
    public StudentDataResponse getStudentById(Long id) {
        return EntityDtoStudentMapper.INSTANCE.studentToStudentDataResponse(findStudentById(id));
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
    public List<StudentDataResponse> getAllStudents() {
        List<StudentEntity> studentResponse = studentRepo.findAll();
        if (studentResponse.isEmpty()) {
        	return new ArrayList<>();
        }
        return studentResponse.stream().map(EntityDtoStudentMapper.INSTANCE::studentToStudentDataResponse).collect(Collectors.toList());
    }

    @Override
    public List<StudentDataResponse> getStudentsByName(String name) {
        List<StudentEntity> studentResponse = studentRepo.findByFirstName(name);
        if (studentResponse == null || studentResponse.isEmpty()) {
        	return new ArrayList<>();
        }
        return studentResponse.stream().map(EntityDtoStudentMapper.INSTANCE::studentToStudentDataResponse).collect(Collectors.toList());
    }

    @Override
    public List<StudentDataResponse> getStudentsBySurname(String surname) {
        List<StudentEntity> studentResponse = studentRepo.findByLastName(surname);
        if (studentResponse == null || studentResponse.isEmpty()) {
        	return new ArrayList<>();
        }
        return studentResponse.stream().map(EntityDtoStudentMapper.INSTANCE::studentToStudentDataResponse).collect(Collectors.toList());
    }

    @Override
    public List<StudentDataResponse> getStudentsByNameAndSurname(String name, String surname) {
        List<StudentEntity> studentResponse = studentRepo.findByFirstNameAndLastName(name, surname);
        if (studentResponse == null || studentResponse.isEmpty()) {
        	return new ArrayList<>();
        }
        return studentResponse.stream().map(EntityDtoStudentMapper.INSTANCE::studentToStudentDataResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public StudentDataResponse updateStudent(StudentUpdateDataRequest student) {
        StudentEntity studentEntity = studentRepo.findById(student.getId())
                .orElseThrow(() -> new StudentNotFoundException("Student with id " + student.getId() + " doesn't exist"));

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
        studentEntity = studentRepo.save(studentEntity);
        return EntityDtoStudentMapper.INSTANCE.studentToStudentDataResponse(studentEntity);
    }

    @Override
    @Transactional
    public StudentDataResponse deleteStudent(Long id) {
        StudentEntity student = findStudentById(id);
        student.getGroups().forEach(group -> group.getStudents().remove(student));
        student.getGroups().clear();
        activityRepo.deleteByStudentId(id);
        paymentRepo.deleteByStudentId(id);
        studentRepo.deleteById(id);
        return EntityDtoStudentMapper.INSTANCE.studentToStudentDataResponse(student);
    }
    
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
	public List<StudentDataResponse> getStudentsByGroup(String name) {
		GroupEntity group = groupRepo.findByName(name);
		List<StudentEntity> students = group.getStudents();
		if (students == null || students.isEmpty()) {
			return new ArrayList<>();
		}
        return students.stream().map(EntityDtoStudentMapper.INSTANCE::studentToStudentDataResponse).collect(Collectors.toList());
	}
}

