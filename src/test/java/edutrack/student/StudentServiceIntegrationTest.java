package edutrack.student;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import edutrack.activityLog.entity.ActivityLogEntity;
import edutrack.activityLog.repository.ActivityLogRepository;
import edutrack.payment.entity.PaymentEntity;
import edutrack.payment.repository.PaymentRepository;
import edutrack.student.constant.LeadStatus;
import edutrack.student.dto.request.StudentCreateRequest;
import edutrack.student.dto.request.StudentUpdateDataRequest;
import edutrack.student.dto.response.StudentDataResponse;
import edutrack.student.entity.StudentEntity;
import edutrack.student.repository.StudentRepository;
import edutrack.student.service.StudentService;

@SpringBootTest
@Sql(scripts = {"classpath:testdata.sql"})
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

        StudentEntity savedStudent = studentRepo.findById(response.getId()).orElse(null);
        assertNotNull(savedStudent);
        assertEquals("Kate", savedStudent.getFirstName());

        List<ActivityLogEntity> logs = activityRepo.findByStudentId(savedStudent.getId());
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
    public void testDeleteStudent() {
    	StudentDataResponse deletedStudent = studentService.getStudentById(STUDENT_ID_DB_H2);
        studentService.deleteStudent(STUDENT_ID_DB_H2);

        assertEquals(STUDENT_ID_DB_H2, deletedStudent.getId());
        assertFalse(studentRepo.findById(STUDENT_ID_DB_H2).isPresent());

        List<ActivityLogEntity> logs = activityRepo.findByStudentId(deletedStudent.getId());
        assertTrue(logs.isEmpty());

        List<PaymentEntity> payments = paymentRepo.findByStudentId(deletedStudent.getId());
        assertTrue(payments.isEmpty());
    }

}