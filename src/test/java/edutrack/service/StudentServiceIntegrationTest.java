package edutrack.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import edutrack.constant.GroupStatus;
import edutrack.constant.LeadStatus;
import edutrack.entity.students.Group;
import edutrack.repository.GroupRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import edutrack.dto.request.students.AddStudentCommentRequest;
import edutrack.dto.request.students.AddStudentPaymentRequest;
import edutrack.dto.request.students.StudentCreateRequest;
import edutrack.dto.request.students.StudentUpdateDataRequest;
import edutrack.dto.response.students.PaymentConfirmationResponse;
import edutrack.dto.response.students.StudentActivityLogResponse;
import edutrack.dto.response.students.StudentDataResponse;
import edutrack.entity.students.ActivityLog;
import edutrack.entity.students.Payment;
import edutrack.entity.students.Student;
import edutrack.repository.ActivityLogRepository;
import edutrack.repository.PaymentRepository;
import edutrack.repository.StudentRepository;

@SpringBootTest
@Transactional
@Rollback
public class StudentServiceIntegrationTest {

    @Autowired
    private StudentService studentService;

    @Autowired
    private StudentRepository studentRepo;

    @Autowired
    private ActivityLogRepository activityRepo;

    @Autowired
    private PaymentRepository paymentRepo;
    @Autowired
    private GroupRepository groupRepo;

    @Test
    public void testCreateStudent() {
        StudentCreateRequest request = new StudentCreateRequest("John", "Doe", "1234567890", "kate@test.com",
                "New York", "Math", "Online", LeadStatus.STUDENT, "Initial comment");

        StudentDataResponse response = studentService.createStudent(request);

        assertNotNull(response.getId());
        assertEquals("John", response.getName());
        assertEquals("Doe", response.getSurname());

        Student savedStudent = studentRepo.findById(response.getId()).orElse(null);
        assertNotNull(savedStudent);
        assertEquals("John", savedStudent.getFirstName());

        List<ActivityLog> logs = activityRepo.findByStudentId(savedStudent.getId());
        assertFalse(logs.isEmpty());
        assertEquals("Initial comment", logs.get(0).getInformation());
    }

    @Test
    public void testGetStudentById() {
        StudentCreateRequest request = new StudentCreateRequest("Jane", "Smith", "0987654321", "kate2@example.com",
                "Los Angeles", "Science", "Referral", LeadStatus.ARCHIVE, null);

        StudentDataResponse createdStudent = studentService.createStudent(request);

        StudentDataResponse fetchedStudent = studentService.getStudentById(createdStudent.getId());

        assertEquals(createdStudent.getId(), fetchedStudent.getId());
        assertEquals("Jane", fetchedStudent.getName());
    }

    @Test
    public void testUpdateStudent() {
        Group groupStudent = new Group();
        groupStudent.setName("Example Group");
        groupStudent.setWhatsApp("example-whatsapp");
        groupStudent.setSkype("example-skype");
        groupStudent.setSlack("example-slack");
        groupStudent.setStatus(GroupStatus.ACTIVE);
        groupStudent.setStartDate(LocalDate.of(2024, 1, 1));
        groupStudent.setExpFinishDate(LocalDate.of(2024, 12, 31));
        groupRepo.save(groupStudent);
        StudentCreateRequest createRequest = new StudentCreateRequest("Tom", "Hardy", "5678901234", "tom.hardy@example.com",
                "Chicago", "History", "Web", LeadStatus.IN_WORK, null);

        StudentDataResponse createdStudent = studentService.createStudent(createRequest);

        StudentUpdateDataRequest updateRequest = new StudentUpdateDataRequest(createdStudent.getId(), "Thomas",
                "Hardy", "5678901234", "tom.h@example.com", "San Francisco",
                "History", "Web", LeadStatus.IN_WORK);

        StudentDataResponse updatedStudent = studentService.updateStudent(updateRequest);

        assertEquals("Thomas", updatedStudent.getName());
        assertEquals("San Francisco", updatedStudent.getCity());
    }

    @Test
    public void testAddStudentComment() {
        StudentCreateRequest createRequest = new StudentCreateRequest("Alice", "Johnson", "5551234567", "alice.johnson@example.com",
                "Boston", "Art", "Social Media", LeadStatus.LEAD, null);

        StudentDataResponse createdStudent = studentService.createStudent(createRequest);

        AddStudentCommentRequest commentRequest = new AddStudentCommentRequest(createdStudent.getId(), LocalDate.now(), "Second comment");

        StudentActivityLogResponse activityLogResponse = studentService.addStudentComment(commentRequest);

        System.out.println(activityLogResponse);
        
        assertNotNull(activityLogResponse);
        assertEquals(2, activityLogResponse.getActivityLog().size());
        assertEquals("Create student data", activityLogResponse.getActivityLog().get(0).getMessage());
        assertEquals("Second comment", activityLogResponse.getActivityLog().get(1).getMessage());
    }

    @Test
    public void testAddStudentPayment() {
        StudentCreateRequest createRequest = new StudentCreateRequest("Bob", "Marley", "4449876543", "bob.marley@example.com",
                "Miami", "Music", "Event", LeadStatus.STUDENT, null);

        StudentDataResponse createdStudent = studentService.createStudent(createRequest);

        AddStudentPaymentRequest paymentRequest = new AddStudentPaymentRequest(
        		createdStudent.getId(), LocalDate.now(),
                "Credit Card", BigDecimal.valueOf(1500.00), 3,"Course Fee");

        PaymentConfirmationResponse paymentResponse = studentService.addStudentPayment(paymentRequest);

        assertNotNull(paymentResponse.getId());
        assertEquals(BigDecimal.valueOf(1500.00), paymentResponse.getAmount());
        assertEquals("Course Fee", paymentResponse.getDetails());
    }

    @Test
    public void testDeleteStudent() {
        StudentCreateRequest createRequest = new StudentCreateRequest("Mike", "Tyson", "3339871234", "mike.tyson@example.com",
                "Las Vegas", "Sports", "Referral", LeadStatus.CONSULTATION, null);

        StudentDataResponse createdStudent = studentService.createStudent(createRequest);

        StudentDataResponse deletedStudent = studentService.deleteStudent(createdStudent.getId());

        assertEquals(createdStudent.getId(), deletedStudent.getId());
        assertFalse(studentRepo.findById(createdStudent.getId()).isPresent());

        List<ActivityLog> logs = activityRepo.findByStudentId(deletedStudent.getId());
        assertTrue(logs.isEmpty());

        List<Payment> payments = paymentRepo.findByStudentId(deletedStudent.getId());
        assertTrue(payments.isEmpty());
    }

    @Test
    public void testCascadeDeleteStudent() {
        StudentCreateRequest createRequest = new StudentCreateRequest("Bruce", "Lee", "2223456789", "bruce.lee@example.com",
                "San Francisco", "Martial Arts", "Referral", LeadStatus.IN_WORK, null);

        StudentDataResponse createdStudent = studentService.createStudent(createRequest);

        AddStudentCommentRequest commentRequest = new AddStudentCommentRequest(createdStudent.getId(), LocalDate.now(), "First training session");
        studentService.addStudentComment(commentRequest);

        AddStudentPaymentRequest paymentRequest = new AddStudentPaymentRequest(createdStudent.getId(), LocalDate.now(),
                "Cash", BigDecimal.valueOf(2000.00), 3,"Training Fee");
        studentService.addStudentPayment(paymentRequest);

        studentService.deleteStudent(createdStudent.getId());
        //org.opentest4j.AssertionFailedError: expected: <true> but was: <false>

        assertFalse(studentRepo.findById(createdStudent.getId()).isPresent());

        List<ActivityLog> logs = activityRepo.findByStudentId(createdStudent.getId());
        assertTrue(logs.isEmpty());

        List<Payment> payments = paymentRepo.findByStudentId(createdStudent.getId());
        assertTrue(payments.isEmpty());
    }
}