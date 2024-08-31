package edutrack.service;

import edutrack.dto.request.students.GroupCreateRequest;
import edutrack.dto.request.students.GroupUpdateDataRequest;
import edutrack.dto.request.students.AddStudentCommentRequest;
import edutrack.dto.request.students.AddStudentPaymentRequest;
import edutrack.dto.request.students.StudentCreateRequest;
import edutrack.dto.request.students.StudentUpdateDataRequest;
import edutrack.dto.response.students.*;
import edutrack.entity.students.ActivityLog;
import edutrack.entity.students.Payment;
import edutrack.entity.students.Student;
import edutrack.exception.EmailAlreadyInUseException;
import edutrack.exception.StudentNotFoundException;
import edutrack.dto.response.students.GroupDataResponse;
import edutrack.dto.response.students.StudentActivityLog;
import edutrack.dto.response.students.StudentActivityLogResponse;
import edutrack.dto.response.students.StudentDataResponse;
import edutrack.dto.response.students.StudentPayment;
import edutrack.dto.response.students.StudentPaymentInfoResponse;
import edutrack.entity.students.Group;
import edutrack.repository.ActivityLogRepository;
import edutrack.repository.GroupRepository;
import edutrack.repository.PaymentRepository;
import edutrack.repository.StudentRepository;
import edutrack.util.EntityDtoMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class StudentService implements IStudent {
    StudentRepository studentRepo;
    ActivityLogRepository activityRepo;
    PaymentRepository paymentRepo;
    GroupRepository groupRepo;
    

    private ActivityLog createActivityLogByStudent(Student studentEntity, String comment) {
        ActivityLog log = new ActivityLog(null, LocalDate.now(), comment, studentEntity);
        return activityRepo.save(log);
    }
	
	private GroupDataResponse toGroupDataResponse(Group group) {
        Boolean deactivateAfter30Days = false;
        if (group.getDeactivateAfter30Days()!=LocalDate.MAX) deactivateAfter30Days = true;
		return new GroupDataResponse(group.getName(), group.getWhatsApp(), group.getSkype(), group.getSlack(),group.getStatus(),
        		group.getStartDate(), group.getExpFinishDate(), group.getLessonsDays(), group.getWebinarsDays(), deactivateAfter30Days, 
        		group.getStudents(),group.getGroupReminders());
	}
	

    private StudentActivityLogResponse toStudentActivityLogResponse(Student student, List<StudentActivityLog> studentActivityLog) {
        return new StudentActivityLogResponse(student.getId(), student.getFirstName(), student.getLastName(),
                student.getPhoneNumber(), student.getEmail(), student.getCity(), student.getCourse(),
                student.getSource(), student.getLeadStatus(), studentActivityLog);
    }

    private StudentPayment toStudentPayment(Payment payment) {
        return new StudentPayment(payment.getDate(), payment.getType(), payment.getAmount(), payment.getDetails());
    }

    private StudentPaymentInfoResponse toStudentPaymentInfoResponse(Student student, List<StudentPayment> studentPayment) {
        return new StudentPaymentInfoResponse(student.getId(), student.getFirstName(), student.getLastName(),
                student.getPhoneNumber(), student.getEmail(), student.getCity(), student.getCourse(),
                student.getSource(), student.getLeadStatus(), studentPayment);
    }

    private Student findStudentById(Long id) {
        return studentRepo.findById(id).orElseThrow(
                () -> new StudentNotFoundException("Student with id " + id + " not found"));
    }

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
    public StudentActivityLogResponse getStudentActivityLog(Long id) {
        Student student = findStudentById(id);
        List<ActivityLog> activityLogs = activityRepo.findByStudentId(id);
        List<StudentActivityLog> studentActivityLog = activityLogs.stream()
        	    .map(log -> new StudentActivityLog(log.getDate(), log.getInformation()))
        	    .collect(Collectors.toList());
        return toStudentActivityLogResponse(student, studentActivityLog);
    }

    @Override
    @Transactional
    public StudentPaymentInfoResponse getStudentPaymentInfo(Long id) {
        Student student = findStudentById(id);
        List<Payment> payments = paymentRepo.findByStudentId(id);
        if (payments == null || payments.isEmpty())
            return new StudentPaymentInfoResponse();
        List<StudentPayment> studentPayment = payments.stream().map(this::toStudentPayment).collect(Collectors.toList());
        return toStudentPaymentInfoResponse(student, studentPayment);
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
    public StudentActivityLogResponse addStudentComment(AddStudentCommentRequest studentComment) {
        Student student = findStudentById(studentComment.getStudentId());
        LocalDate date = studentComment.getDate()==null?LocalDate.now():studentComment.getDate();
        ActivityLog activityLog = new ActivityLog(null, date, studentComment.getMessage(), student);
        activityLog = activityRepo.save(activityLog);
        return getStudentActivityLog(studentComment.getStudentId());
    }

    @Override
    @Transactional
    public PaymentConfirmationResponse addStudentPayment(AddStudentPaymentRequest studentPayment) {
        Student student = findStudentById(studentPayment.getStudentId());
        Payment savedPayment = new Payment(null, studentPayment.getDate(), studentPayment.getType(), studentPayment.getAmount(), studentPayment.getInstallments(), studentPayment.getDetails(), student);
        savedPayment = paymentRepo.save(savedPayment);
        PaymentConfirmationResponse response = new PaymentConfirmationResponse(
                savedPayment.getId(),             // ID платежа
                savedPayment.getDate(),
                savedPayment.getType(),
                savedPayment.getAmount(),
                savedPayment.getInstallments(),
                savedPayment.getDetails()
        );
        return response;
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
	@Transactional
	public GroupDataResponse updateGroup(GroupUpdateDataRequest groupRequest) {
        LocalDate deactivateAfter30Days = LocalDate.MAX;
        Group groupResponse = groupRepo.findByName(groupRequest.getName());
        if (groupResponse != null)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Group with name " + groupRequest.getName() + " is already exists");
		if (groupRequest.getDeactivateAfter30Days() && groupRequest.getExpFinishDate()==null)
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "EFD must not be null to deactivate group after 30 days");
        Group groupEntity = EntityDtoMapper.INSTANCE.groupUpdateRequestToGroup(groupRequest);
        if (groupRequest.getDeactivateAfter30Days()) {
        	deactivateAfter30Days = groupRequest.getExpFinishDate().plusDays(30);
        }
        groupEntity.setDeactivateAfter30Days(deactivateAfter30Days);
        groupRepo.save(groupEntity);
        return toGroupDataResponse(groupEntity);
	}

}

