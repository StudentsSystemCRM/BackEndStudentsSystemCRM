package edutrack.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import edutrack.constant.GroupStatus;
import edutrack.constant.LeadStatus;
import edutrack.entity.students.Group;
import edutrack.repository.GroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import edutrack.dto.request.students.StudentCreateRequest;
import edutrack.dto.request.students.StudentUpdateDataRequest;
import edutrack.dto.response.students.StudentDataResponse;
import edutrack.entity.students.ActivityLog;
import edutrack.entity.students.Student;
import edutrack.exception.EmailAlreadyInUseException;
import edutrack.exception.StudentNotFoundException;
import edutrack.repository.ActivityLogRepository;
import edutrack.repository.PaymentRepository;
import edutrack.repository.StudentRepository;

@ExtendWith(MockitoExtension.class)
public class StudentServiceTest {

    @Mock
    StudentRepository studentRepo;

    @Mock
    GroupRepository groupRepo;

    @Mock
    ActivityLogRepository activityRepo;

    @Mock
    PaymentRepository paymentRepo;

    @InjectMocks
    StudentService studentService;

    Group groupStudent;
    Student student;
    StudentCreateRequest request;
    StudentUpdateDataRequest updateDataRequest;

    @BeforeEach
    public void setUp() {
        groupStudent = new Group(
                "Example Group",
                "example-whatsapp",
                "example-skype",
                "example-slack",
                GroupStatus.ACTIVE,
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 12, 31),
                LocalDate.of(2024, 6, 30),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                "",
                ""
        );
       // List<Group> groups = List.of(groupStudent);
//       when(groupRepo.findById("Example Group")).thenReturn(Optional.of(groupStudent));
//       when(groupRepo.save(any(Group.class))).thenReturn(groupStudent);

        student = new Student(1L, "John", "Doe", "123456789",
                "john.doe@example.com", "City", "Course", "Source",
                LeadStatus.CONSULTATION, "testGroup",
                16000, groupStudent,null,null,null,"","");
        request = new StudentCreateRequest("John", "Doe",
                "123456789", "john.doe@example.com", "City", "Course",
                "Source", LeadStatus.IN_WORK, "Create comment");
        updateDataRequest = new StudentUpdateDataRequest(1L, "John", "Doe",
                "123456789", "john.doe@example.com", "New City", "New Course", "New Source",
                LeadStatus.STUDENT);

        // Настройка поведения репозитория студентов
//        when(studentRepo.findByEmail("john.doe@example.com")).thenReturn(null);
//        when(studentRepo.save(any(Student.class))).thenReturn(student);
    }

    @Test
    public void testGetStudentById_Success() {
        when(studentRepo.findById(1L)).thenReturn(Optional.of(student));
        StudentDataResponse response = studentService.getStudentById(1L);
        assertNotNull(response);
        assertEquals(student.getFirstName(), response.getName());
        assertEquals(student.getLastName(), response.getSurname());
        assertEquals(student.getCity(), response.getCity());
        assertEquals(student.getCourse(), response.getCourse());
        assertEquals(student.getEmail(), response.getEmail());
        assertEquals(student.getId(), response.getId());
        assertEquals(student.getLeadStatus(), response.getLeadStatus());
        assertEquals(student.getPhoneNumber(), response.getPhone());
        verify(studentRepo, times(1)).findById(1L);
    }

    @Test
    public void testGetStudentById_NotFound() {
        when(studentRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(StudentNotFoundException.class, () -> {
            studentService.getStudentById(1L);
        });

        verify(studentRepo, times(1)).findById(1L);
    }

    @Test
    public void testCreateStudent_Success() {
        when(studentRepo.save(any(Student.class))).thenReturn(student);
       
        StudentDataResponse response = studentService.createStudent(request);

        assertNotNull(response);
        verify(studentRepo, times(1)).findByEmail("john.doe@example.com");
        verify(studentRepo, times(1)).save(any(Student.class));
        verify(activityRepo, times(1)).save(any(ActivityLog.class));
        
        assertEquals(student.getFirstName(), response.getName());
        assertEquals(student.getLastName(), response.getSurname());
        assertEquals(student.getCity(), response.getCity());
        assertEquals(student.getCourse(), response.getCourse());
        assertEquals(student.getEmail(), response.getEmail());
        assertEquals(student.getId(), response.getId());
        assertEquals(student.getLeadStatus(), response.getLeadStatus());
        assertEquals(student.getPhoneNumber(), response.getPhone());
    }

    
    @Test
    public void testCreateStudent_EmailAlreadyExists() {
        when(studentRepo.findByEmail("john.doe@example.com")).thenReturn(new Student());

        assertThrows(EmailAlreadyInUseException.class, () -> {
            studentService.createStudent(request);
        });

        verify(studentRepo, times(1)).findByEmail("john.doe@example.com");
        verify(studentRepo, times(0)).save(any(Student.class));
        verify(activityRepo, times(0)).save(any(ActivityLog.class));
    }
    
    @Test
    public void testUpdateStudent_Success() {
        Student existingStudent = new Student(1L, "John", "Doe",
                "123456789", "john.doe@example.com", "City",
                "Course", "Source", LeadStatus.IN_WORK, "testGroup",
                16000, null,null,null,null,"","");

        when(studentRepo.findById(1L)).thenReturn(Optional.of(existingStudent));
        when(studentRepo.save(any(Student.class))).thenReturn(existingStudent);
  
        StudentDataResponse response = studentService.updateStudent(updateDataRequest);

        assertNotNull(response);
        verify(studentRepo, times(1)).findById(1L);
        verify(studentRepo, times(1)).save(existingStudent);
    }

    @Test
    public void testUpdateStudent_NotFound() {
       assertThrows(StudentNotFoundException.class, () -> {
            studentService.updateStudent(updateDataRequest);
        });

        verify(studentRepo, times(1)).findById(1L);
        verify(studentRepo, times(0)).save(any(Student.class));
    }

    @Test
    public void testDeleteStudent_Success() {
        when(studentRepo.findById(1L)).thenReturn(Optional.of(student));

        studentService.deleteStudent(1L);

        verify(studentRepo, times(1)).findById(1L);
        verify(studentRepo, times(1)).deleteById(1L);
        verify(activityRepo, times(1)).deleteByStudentId(1L);
        verify(paymentRepo, times(1)).deleteByStudentId(1L);
        
    }

    @Test
    public void testDeleteStudent_NotFound() {
        when(studentRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(StudentNotFoundException.class, () -> {
            studentService.deleteStudent(1L);
        });

        verify(studentRepo, times(1)).findById(1L);
        verify(studentRepo, times(0)).delete(any(Student.class));
    }
}