package edutrack.modul.student.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import edutrack.exception.EmailAlreadyInUseException;
import edutrack.exception.StudentNotFoundException;
import edutrack.modul.activityLog.entity.ActivityLog;
import edutrack.modul.activityLog.repository.ActivityLogRepository;
import edutrack.modul.payment.repository.PaymentRepository;
import edutrack.modul.student.dto.request.StudentCreateRequest;
import edutrack.modul.student.dto.request.StudentUpdateDataRequest;
import edutrack.modul.student.dto.response.StudentDataResponse;
import edutrack.modul.student.entity.Student;
import edutrack.modul.student.repository.StudentRepository;
import edutrack.util.EntityDtoMapper;
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

    @Override
    public StudentDataResponse getStudentById(Long id) {
        return EntityDtoMapper.INSTANCE.studentToStudentDataResponse(findStudentById(id));
    }

    @Override
    @Transactional
    public StudentDataResponse createStudent(StudentCreateRequest student) {
        if (studentRepo.findByEmail(student.getEmail()) != null) {
            throw new EmailAlreadyInUseException("Student with email " + student.getEmail() + " already exists.");
        }

        Student studentEntity = EntityDtoMapper.INSTANCE.studentCreateRequestToStudent(student);
        studentEntity = studentRepo.save(studentEntity);
        String comment = student.getComment() == null? "Create student data" : student.getComment();
        createActivityLogByStudent(studentEntity, comment);
        return EntityDtoMapper.INSTANCE.studentToStudentDataResponse(studentEntity);
    }

    @Override
    public List<StudentDataResponse> getAllStudents() {
        List<Student> studentResponse = studentRepo.findAll();
        if (studentResponse.isEmpty())
            return new ArrayList<>();
        return studentResponse.stream().map(EntityDtoMapper.INSTANCE::studentToStudentDataResponse).collect(Collectors.toList());
    }

    @Override
    public List<StudentDataResponse> getStudentsByName(String name) {
        List<Student> studentResponse = studentRepo.findByFirstName(name);
        if (studentResponse == null || studentResponse.isEmpty())
            return new ArrayList<>();
        return studentResponse.stream().map(EntityDtoMapper.INSTANCE::studentToStudentDataResponse).collect(Collectors.toList());
    }

    @Override
    public List<StudentDataResponse> getStudentsBySurname(String surname) {
        List<Student> studentResponse = studentRepo.findByLastName(surname);
        if (studentResponse == null || studentResponse.isEmpty())
            return new ArrayList<>();
        return studentResponse.stream().map(EntityDtoMapper.INSTANCE::studentToStudentDataResponse).collect(Collectors.toList());
    }

    @Override
    public List<StudentDataResponse> getStudentsByNameAndSurname(String name, String surname) {
        List<Student> studentResponse = studentRepo.findByFirstNameAndLastName(name, surname);
        if (studentResponse == null || studentResponse.isEmpty())
            return new ArrayList<>();
        return studentResponse.stream().map(EntityDtoMapper.INSTANCE::studentToStudentDataResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public StudentDataResponse updateStudent(StudentUpdateDataRequest student) {
        Student studentEntity = studentRepo.findById(student.getId())
                .orElseThrow(() -> new StudentNotFoundException("Student with id " + student.getId() + " doesn't exist"));

        if (!student.getEmail().equals(studentEntity.getEmail())) {
            Student existingStudent = studentRepo.findByEmail(student.getEmail());
            if (existingStudent != null)
                throw new EmailAlreadyInUseException("Email " + student.getEmail() + " is already in use by another student.");
        }

        studentEntity.setFirstName(student.getName());
        studentEntity.setLastName(student.getSurname());
        studentEntity.setPhoneNumber(student.getPhone());
        studentEntity.setEmail(student.getEmail());
        if(student.getCity() != null)
        	studentEntity.setCity(student.getCity());
        if(student.getCourse() != null)
        	studentEntity.setCourse(student.getCourse());
        if(student.getSource() != null)
        	studentEntity.setSource(student.getSource());
        if(student.getLeadStatus() != null)
        	studentEntity.setLeadStatus(student.getLeadStatus());
        studentEntity = studentRepo.save(studentEntity);
        return EntityDtoMapper.INSTANCE.studentToStudentDataResponse(studentEntity);
    }

    @Override
    @Transactional
    public StudentDataResponse deleteStudent(Long id) {
        Student student = findStudentById(id);
        activityRepo.deleteByStudentId(id);
        paymentRepo.deleteByStudentId(id);
        studentRepo.deleteById(id);
        return EntityDtoMapper.INSTANCE.studentToStudentDataResponse(student);
    }
    
    private ActivityLog createActivityLogByStudent(Student studentEntity, String comment) {
        ActivityLog log = new ActivityLog(null, LocalDate.now(), comment, studentEntity);
        return activityRepo.save(log);
    }	

    private Student findStudentById(Long id) {
        return studentRepo.findById(id).orElseThrow(
                () -> new StudentNotFoundException("Student with id " + id + " not found"));
    }


}

