package edutrack.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.test.context.jdbc.Sql;

import edutrack.constant.LeadStatus;
import edutrack.dto.request.students.AddStudentCommentRequest;
import edutrack.dto.request.students.AddStudentPaymentRequest;
import edutrack.dto.request.students.StudentCreateRequest;
import edutrack.dto.request.students.StudentUpdateDataRequest;
import edutrack.dto.response.students.PaymentConfirmationResponse;
import edutrack.dto.response.students.StudentActivityLogResponse;
import edutrack.dto.response.students.StudentDataResponse;
import edutrack.dto.response.students.StudentPaymentInfoResponse;
import edutrack.entity.students.ActivityLog;
import edutrack.entity.students.Payment;
import edutrack.entity.students.Student;
import edutrack.repository.ActivityLogRepository;
import edutrack.repository.PaymentRepository;
import edutrack.repository.StudentRepository;

@SpringBootTest
@Sql(scripts = {"classpath:testdata.sql"})
//student in db (2, 'Jane', 'Smith', '0987654321', 'kate2@example.com', 'Los Angeles', 'Science', 'Referral', 'Prospect')
public class StudentServiceIntegrationTest {

    @Autowired
    private StudentService studentService;

    @Autowired
    private StudentRepository studentRepo;

    @Autowired
    private ActivityLogRepository activityRepo;

    @Autowired
    private PaymentRepository paymentRepo;
    
    static final Long STUDENT_ID_DB_H2 = 2L;

    @Test
    public void testCreateStudent() {
        StudentCreateRequest request = new StudentCreateRequest("Kate", "Gan", "1234567890", "kate@test.com",
                "New York", "Math", "Online", LeadStatus.LEAD, "Initial comment");

        StudentDataResponse response = studentService.createStudent(request);

        assertNotNull(response.getId());
        assertEquals("Kate", response.getName());
        assertEquals("Gan", response.getSurname());

        Student savedStudent = studentRepo.findById(response.getId()).orElse(null);
        assertNotNull(savedStudent);
        assertEquals("Kate", savedStudent.getFirstName());

        List<ActivityLog> logs = activityRepo.findByStudentId(savedStudent.getId());
        assertFalse(logs.isEmpty());
        assertEquals("Initial comment", logs.get(0).getInformation());
    }

    @Test
    public void testGetStudentById() {
        StudentDataResponse fetchedStudent = studentService.getStudentById(STUDENT_ID_DB_H2);
        assertEquals(STUDENT_ID_DB_H2, fetchedStudent.getId());
        assertEquals("Jane", fetchedStudent.getName());
    }

    @Test
    public void testUpdateStudent() {
        StudentUpdateDataRequest updateRequest = new StudentUpdateDataRequest(STUDENT_ID_DB_H2, "Thomas",
                "Hardy", "5678901234", "tom.h@example.com", "San Francisco", "History", "Web", LeadStatus.LEAD);

        StudentDataResponse updatedStudent = studentService.updateStudent(updateRequest);

        assertEquals("Thomas", updatedStudent.getName());
        assertEquals("San Francisco", updatedStudent.getCity());
    }

    @Test
    public void testAddStudentComment() {

        AddStudentCommentRequest commentRequest = new AddStudentCommentRequest(STUDENT_ID_DB_H2, LocalDate.now(), "Second comment");
        StudentActivityLogResponse activityLogResponse = studentService.addStudentComment(commentRequest);
        
        assertNotNull(activityLogResponse);
        assertEquals(1, activityLogResponse.getActivityLog().size());
        assertEquals("Second comment", activityLogResponse.getActivityLog().get(0).getMessage());
    }

    @Test
    public void testAddStudentPayment() {
        AddStudentPaymentRequest paymentRequest = new AddStudentPaymentRequest(
        		STUDENT_ID_DB_H2, LocalDate.now(),
                "Credit Card", BigDecimal.valueOf(1500.00), 3,"Course Fee");

        PaymentConfirmationResponse paymentResponse = studentService.addStudentPayment(paymentRequest);        
        StudentPaymentInfoResponse resp = studentService.getStudentPaymentInfo(STUDENT_ID_DB_H2);

        assertNotNull(paymentResponse.getId());
        assertEquals(BigDecimal.valueOf(1500.00), paymentResponse.getAmount());
        assertEquals("Course Fee", paymentResponse.getDetails());
        assertEquals(resp.getPaymentInfo().size(), 1);
        assertTrue(resp.getPaymentInfo().get(0).getAmount().compareTo(BigDecimal.valueOf(1500.00))==0);
    }

    @Test
    public void testDeleteStudent() {
        StudentDataResponse deletedStudent = studentService.deleteStudent(STUDENT_ID_DB_H2);

        assertEquals(STUDENT_ID_DB_H2, deletedStudent.getId());
        assertFalse(studentRepo.findById(STUDENT_ID_DB_H2).isPresent());

        List<ActivityLog> logs = activityRepo.findByStudentId(deletedStudent.getId());
        assertTrue(logs.isEmpty());

        List<Payment> payments = paymentRepo.findByStudentId(deletedStudent.getId());
        assertTrue(payments.isEmpty());
    }

    @Test
    public void testCascadeDeleteStudent() {
        AddStudentCommentRequest commentRequest = new AddStudentCommentRequest(STUDENT_ID_DB_H2, LocalDate.now(), "First training session");
        studentService.addStudentComment(commentRequest);
        
        AddStudentPaymentRequest paymentRequest = new AddStudentPaymentRequest(STUDENT_ID_DB_H2, LocalDate.now(),
                "Cash", BigDecimal.valueOf(2000.00), 2,"Training Fee");
        studentService.addStudentPayment(paymentRequest);
        
        List<Payment> payments =  paymentRepo.findByStudentId(STUDENT_ID_DB_H2);
        assertFalse(payments.isEmpty());
        List<ActivityLog> logs = activityRepo.findByStudentId(STUDENT_ID_DB_H2);
        assertFalse(logs.isEmpty());

        studentService.deleteStudent(STUDENT_ID_DB_H2);

        assertFalse(studentRepo.findById(STUDENT_ID_DB_H2).isPresent());

        logs = activityRepo.findByStudentId(STUDENT_ID_DB_H2);
        assertTrue(logs.isEmpty());

        payments = paymentRepo.findByStudentId(STUDENT_ID_DB_H2);
        assertTrue(payments.isEmpty());
    }
}