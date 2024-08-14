package edutrack.service;

import edutrack.dto.request.students.AddStudentCommentRequest;
import edutrack.dto.request.students.AddStudentPaymentRequest;
import edutrack.dto.request.students.StudentCreateRequest;
import edutrack.dto.request.students.StudentUpdateDataRequest;
import edutrack.dto.response.students.StudentActivityLog;
import edutrack.dto.response.students.StudentActivityLogResponse;
import edutrack.dto.response.students.StudentDataResponse;
import edutrack.dto.response.students.StudentPayment;
import edutrack.dto.response.students.StudentPaymentInfoResponse;
import edutrack.entity.students.ActivityLog;
import edutrack.entity.students.Group;
import edutrack.entity.students.Payment;
import edutrack.entity.students.Student;
import edutrack.repository.ActivityLogRepository;
import edutrack.repository.PaymentRepository;
import edutrack.repository.StudentRepository;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentService implements IStudent {
    @Autowired
    StudentRepository studentRepo;
    @Autowired
    ActivityLogRepository activityRepo;
    @Autowired
    PaymentRepository paymentRepo;

    private static final Long default_id = (long) 0;

    private Student toStudent(StudentCreateRequest studentRequest) {
        ActivityLog log = new ActivityLog();
        Payment pay = new Payment();
        List<Student> st = new ArrayList<Student>();
        Group group = new Group("NoGroup", "", "", "", "", LocalDate.now(), LocalDate.now(), LocalDate.now(), st);
        Student student = new Student(default_id, studentRequest.getName(), studentRequest.getSurname(), studentRequest.getPhone(), studentRequest.getEmail(),
                studentRequest.getCity(), studentRequest.getCourse(), studentRequest.getSource(), studentRequest.getLeadStatus(), group, List.of(log), List.of(pay));
        return student;
    }

    private StudentDataResponse toStudentDataResponse(Student student) {
        return new StudentDataResponse(student.getId().intValue(), student.getFirstName(), student.getLastName(),
                student.getPhoneNumber(), student.getEmail(), student.getCity(), student.getCourse(),
                student.getSource(), student.getLeadStatus());
    }

    private ActivityLog createActivityLogByStudentEmail(String email, String comment) {
        Student student = studentRepo.findByEmail(email);
        ActivityLog log = new ActivityLog(student.getId(), LocalDate.now(), comment, student);
        activityRepo.save(log);
        return log;
    }

    private StudentActivityLog toStudentActivityLog(ActivityLog activityLog) {
        return new StudentActivityLog(activityLog.getDate(), activityLog.getInformation());
    }

    private StudentActivityLogResponse toStudentActivityLogResponse(Student student, List<StudentActivityLog> studentActivityLog) {
        return new StudentActivityLogResponse(student.getId().intValue(), student.getFirstName(), student.getLastName(),
                student.getPhoneNumber(), student.getEmail(), student.getCity(), student.getCourse(),
                student.getSource(), student.getLeadStatus(), studentActivityLog);
    }

    private StudentPayment toStudentPayment(Payment payment) {
        return new StudentPayment(payment.getDate(), payment.getType(), payment.getAmount(), payment.getDetails());
    }

    private StudentPaymentInfoResponse toStudentPaymentInfoResponse(Student student, List<StudentPayment> studentPayment) {
        return new StudentPaymentInfoResponse(student.getId().intValue(), student.getFirstName(), student.getLastName(),
                student.getPhoneNumber(), student.getEmail(), student.getCity(), student.getCourse(),
                student.getSource(), student.getLeadStatus(), studentPayment);
    }

    private Student findStudentById(Integer id) {
        Student student = studentRepo.findById(Long.valueOf(id)).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student with id " + id + " not found"));
        return student;
    }

    @Override
    public StudentDataResponse getStudentById(Integer id) {
        return toStudentDataResponse(findStudentById(id));
    }

    @Override
    @Transactional
    public StudentDataResponse createStudent(StudentCreateRequest student) {
        Student studentResponse = studentRepo.findByEmail(student.getEmail());
        if (studentResponse != null)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Student with email " + student.getEmail() + " is already exists");
        Student studentRequest = toStudent(student);
        studentRepo.save(studentRequest);
        Long id = createActivityLogByStudentEmail(student.getEmail(), student.getComment()).getId();
        studentRequest.setId(id);
        return toStudentDataResponse(studentRequest);
    }

    @Override
    public List<StudentDataResponse> getAllStudents() {
        List<Student> studentResponse = studentRepo.findAll();
        if (studentResponse == null || studentResponse.isEmpty())
            return new ArrayList<StudentDataResponse>();
        return studentResponse.stream().map(c -> toStudentDataResponse(c)).collect(Collectors.toList());
    }

    @Override
    public List<StudentDataResponse> getStudentsByName(String name) {
        List<Student> studentResponse = studentRepo.findByFirstName(name);
        if (studentResponse == null || studentResponse.isEmpty())
            return new ArrayList<StudentDataResponse>();
        return studentResponse.stream().map(c -> toStudentDataResponse(c)).collect(Collectors.toList());
    }

    @Override
    public List<StudentDataResponse> getStudentsBySurname(String surname) {
        List<Student> studentResponse = studentRepo.findByLastName(surname);
        if (studentResponse == null || studentResponse.isEmpty())
            return new ArrayList<StudentDataResponse>();
        return studentResponse.stream().map(c -> toStudentDataResponse(c)).collect(Collectors.toList());
    }

    @Override
    public List<StudentDataResponse> getStudentsByNameAndSurname(String name, String surname) {
        List<Student> studentResponse = studentRepo.findByFirstNameAndLastName(name, surname);
        if (studentResponse == null || studentResponse.isEmpty())
            return new ArrayList<StudentDataResponse>();
        return studentResponse.stream().map(c -> toStudentDataResponse(c)).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public StudentActivityLogResponse getStudentActivityLog(Integer id) {
        Student student = findStudentById(id);
        List<ActivityLog> activityLogs = student.getActivityLogs();
        if (activityLogs == null || activityLogs.isEmpty())
            return new StudentActivityLogResponse();
        List<StudentActivityLog> studentActivityLog = activityLogs.stream().map(c -> toStudentActivityLog(c)).collect(Collectors.toList());
        return toStudentActivityLogResponse(student, studentActivityLog);
    }

    @Override
    @Transactional
    public StudentPaymentInfoResponse getStudentPaymentInfo(Integer id) {
        Student student = findStudentById(id);
        List<Payment> payments = student.getPayments();
        if (payments == null || payments.isEmpty())
            return new StudentPaymentInfoResponse();
        List<StudentPayment> studentPayment = payments.stream().map(c -> toStudentPayment(c)).collect(Collectors.toList());
        return toStudentPaymentInfoResponse(student, studentPayment);
    }

    @Override
    @Transactional
    public StudentDataResponse updateStudent(StudentUpdateDataRequest student) {
        Student studentResponse = studentRepo.findByEmail(student.getEmail());
        if (studentResponse == null)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Student with email " + student.getEmail() + " doesn't exsists");
        studentResponse.setId(Long.valueOf(student.getId()));
        studentResponse.setFirstName(student.getName());
        studentResponse.setLastName(student.getSurname());
        studentResponse.setPhoneNumber(student.getPhone());
        studentResponse.setEmail(student.getEmail());
        studentResponse.setCity(student.getCity());
        studentResponse.setCourse(student.getCourse());
        studentResponse.setSource(student.getSource());
        studentResponse.setLeadStatus(student.getLeadStatus());
        studentRepo.save(studentResponse);
        return toStudentDataResponse(studentResponse);
    }

    @Override
    @Transactional
    public StudentActivityLogResponse addStudentComment(AddStudentCommentRequest studentComment) {
        Long studentId = Long.valueOf(studentComment.getStudentId());
        Student student = findStudentById(studentId.intValue());
        ActivityLog activityLog = new ActivityLog(default_id, studentComment.getDate(), studentComment.getMessage(), student);
        activityRepo.save(activityLog);
        return getStudentActivityLog(studentId.intValue());
    }

    @Override
    @Transactional
    public StudentPaymentInfoResponse addStudentPayment(AddStudentPaymentRequest studentPayment) {
        Long studentId = Long.valueOf(studentPayment.getStudentId());
        Student student = findStudentById(studentId.intValue());
        Payment payment = new Payment(default_id, studentPayment.getDate(), studentPayment.getType(), studentPayment.getAmount(), studentPayment.getDetails(), student);
        paymentRepo.save(payment);
        return getStudentPaymentInfo(studentId.intValue());
    }

    @Override
    @Transactional
    public StudentDataResponse deleteStudent(Integer id) {
        Student student = findStudentById(id);
        List<ActivityLog> activityLogs = student.getActivityLogs();
        if (activityLogs.size() > 0)
            for (ActivityLog activityLog : activityLogs) {
                activityRepo.deleteById(activityLog.getId());
            }
        List<Payment> payments = student.getPayments();
        if (payments.size() > 0)
            for (Payment payment : payments) {
                paymentRepo.deleteById(payment.getId());
            }
        studentRepo.deleteById(Long.valueOf(id));
        return toStudentDataResponse(student);
    }
}
