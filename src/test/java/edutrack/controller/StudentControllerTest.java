package edutrack.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edutrack.constant.LeadStatus;
import edutrack.dto.request.payment.AddPaymentRequest;
import edutrack.dto.request.student.StudentCreateRequest;
import edutrack.dto.response.payment.SinglePayment;
import edutrack.dto.response.payment.StudentPaymentInfoResponse;
import edutrack.dto.response.student.StudentDataResponse;
import edutrack.repository.*;
import edutrack.security.JwtTokenCreator;
import edutrack.security.JwtTokenValidator;
import edutrack.security.SecurityConfig;
import edutrack.service.StudentService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.mockito.Mockito.when;

@WebMvcTest(StudentController.class)
@Import({JwtTokenValidator.class, JwtTokenCreator.class, SecurityConfig.class})
@AutoConfigureMockMvc(addFilters = false)

public class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentService studentService;
    @MockBean
    private UserRepository userRepository;

    @MockBean
    private GroupRepository groupRepository;
    @MockBean
    private StudentRepository studentRepository;

    @MockBean
    private PaymentRepository paymentRepository;

    @MockBean
    private ActivityLogRepository activityLogRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllStudents() throws Exception {
        mockMvc.perform(get("/api/students"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetStudentById() throws Exception {
        Long studentId = 1L;
        StudentDataResponse mockResponse = new StudentDataResponse(
                studentId,
                "John",
                "Doe",
                "1234567890",
                "john.doe@example.com",
                "City",
                "Course",
                "Source",
                LeadStatus.IN_WORK
        );
        Mockito.when(studentService.getStudentById(studentId)).thenReturn(mockResponse);

        mockMvc.perform(get("/api/students/{id}", studentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(studentId))
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.surname").value("Doe"));
    }


    @Test
    public void testCreateStudent() throws Exception {
        StudentCreateRequest request = new StudentCreateRequest(
                "John", "Doe", "1234567890", "john.doe@example.com",
                "City", "Course", "Source", LeadStatus.STUDENT,
                "Comment" );
        StudentDataResponse response = new StudentDataResponse();
        response.setId(1L);

        Mockito.when(studentService.createStudent(any(StudentCreateRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/students/create_student")
                        .contentType("application/json")
                        .content(asJsonString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.getId()));
    }

    @Test
    public void testGetStudentsByName() throws Exception {
        String name = "John";
        List<StudentDataResponse> mockResponse = new ArrayList<>();
        mockResponse.add(new StudentDataResponse(1L, name, "Doe", "1234567890", "john.doe@example.com", "New York", "Engineering", "Website", LeadStatus.STUDENT));
        mockResponse.add(new StudentDataResponse(2L, name, "Smith", "0987654321", "john.smith@example.com", "Los Angeles", "Mathematics", "Referral", LeadStatus.IN_WORK));

        Mockito.when(studentService.getStudentsByName(eq(name))).thenReturn(mockResponse);

        mockMvc.perform(get("/api/students/name")
                        .param("name", name))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(name))
                .andExpect(jsonPath("$[1].name").value(name))
                .andExpect(jsonPath("$[0].surname").value("Doe"))
                .andExpect(jsonPath("$[1].surname").value("Smith"));
    }

    @Test
    public void testGetStudentsByName_NoMatches() throws Exception {
        String name = "NonexistentName";
        List<StudentDataResponse> mockResponse = new ArrayList<>();

        Mockito.when(studentService.getStudentsByName(eq(name))).thenReturn(mockResponse);

        mockMvc.perform(get("/api/students/name")
                        .param("name", name))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    public void testGetStudentsBySurname() throws Exception {
        String surname = "Doe";
        List<StudentDataResponse> mockResponse = new ArrayList<>();
        mockResponse.add(new StudentDataResponse(1L, "John", surname, "1234567890",
                "john.doe@example.com", "New York", "Engineering",
                "Website", LeadStatus.STUDENT));
        mockResponse.add(new StudentDataResponse(2L, "Jane", surname, "0987654321",
                "jane.doe@example.com", "New York", "Engineering",
                "Website", LeadStatus.CONSULTATION));

        Mockito.when(studentService.getStudentsBySurname(eq(surname))).thenReturn(mockResponse);

        mockMvc.perform(get("/api/students/surname")
                        .param("surname", surname))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].surname").value(surname))
                .andExpect(jsonPath("$[1].surname").value(surname));
    }

    @Test
    public void testGetStudentsByNameAndSurname() throws Exception {
        String name = "John";
        String surname = "Doe";
        List<StudentDataResponse> mockResponse = new ArrayList<>();
        mockResponse.add(new StudentDataResponse(1L, name, surname, "1234567890",
                "john.doe@example.com", "New York", "Engineering",
                "Website", LeadStatus.CONSULTATION));
        mockResponse.add(new StudentDataResponse(2L, name, surname, "0987654321",
                "john.doe@example.com", "New York", "Engineering",
                "Website", LeadStatus.SAVE_FOR_LATER));

        Mockito.when(studentService.getStudentsByNameAndSurname(eq(name), eq(surname))).thenReturn(mockResponse);

        mockMvc.perform(get("/api/students/name_and_surname")
                        .param("name", name)
                        .param("surname", surname))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(name))
                .andExpect(jsonPath("$[0].surname").value(surname))
                .andExpect(jsonPath("$[1].name").value(name))
                .andExpect(jsonPath("$[1].surname").value(surname));
    }

    @Test
    public void testUpdateStudent() throws Exception {
        mockMvc.perform(put("/api/students/update_student_information")
                        .contentType("application/json")
                        .content("{\"id\":1,\"name\":\"John\",\"surname\":\"Doe\"}"))
                .andExpect(status().isOk());
    }

    @Test
    public void testAddStudentComment() throws Exception {
    	mockMvc.perform(post("/api/students/comment")
                .contentType("application/json")
                .content("{\"studentId\":1,\"message\":\"Good student\"}"))
        		.andExpect(status().isOk());
    }

    @Test
    public void testAddStudentPayment() throws Exception {

        AddPaymentRequest request = new AddPaymentRequest();
        request.setStudentId(123L);
        request.setDate(LocalDate.of(2024, 8, 23));
        request.setType("tuition");
        request.setAmount(BigDecimal.valueOf(100.0));
        request.setDetails("Payment for August semester");

        StudentPaymentInfoResponse response = new StudentPaymentInfoResponse(
                1L,
                "John",
                "Doe",
                "123-456-7890",
                "john.doe@example.com",
                "Sample City",
                "Sample Course",
                "Sample Source",
                LeadStatus.STUDENT,
                List.of(new SinglePayment(
                		1L,
                        LocalDate.of(2024, 8, 23),
                        "tuition",
                        BigDecimal.valueOf(100.0),
                        12,
                        "Payment for August semester"
                ))
        );

        when(studentService.addStudentPayment(any(AddPaymentRequest.class)))
                .thenReturn(response);

                mockMvc.perform(post("/api/students/payment")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath
                                        ("$.id").value(1))
                                .andExpect(jsonPath
                                		("$.paymentInfo[0].date").value("2024-08-23"))

                                .andExpect(jsonPath
                                        ("$.paymentInfo[0].type").value("tuition"))

                                .andExpect(jsonPath(
                                        "$.paymentInfo[0].amount").value(100.0))
                                .andExpect(jsonPath(

                                        "$.paymentInfo[0].details").value("Payment for August semester"));
    }
    @Test
    public void testGetStudentPaymentInfo() throws Exception {
        Long studentId = 1L;
        LeadStatus leadStatus = LeadStatus.STUDENT;
     StudentPaymentInfoResponse response = new StudentPaymentInfoResponse(
                studentId,
                "John",
                "Doe",
                "123-456-7890",
                "john.doe@example.com",
                "Sample City",
                "Sample Course",
                "Sample Source",
                leadStatus,
                List.of(new SinglePayment(
                		1L,
                        LocalDate.of(2024, 8, 23),
                        "tuition",
                        BigDecimal.valueOf(100.0),
                        12,
                        "Payment for August semester"
                ))
        );

        when(studentService.getStudentPaymentInfo(anyLong())).thenReturn(response);

                mockMvc.perform(get
                                ("/api/students/{id}/payments", studentId)
                                .contentType
                                        ("application/json"))
                        .andExpect(status().isOk())
                .andExpect(jsonPath
                        ("$.id").value(studentId))
                .andExpect(jsonPath(

                        "$.name").value("John"))
                .andExpect(jsonPath
                        ("$.surname").value("Doe"))
                        .andExpect(jsonPath
                                ("$.phone").value("123-456-7890"))
                .andExpect(jsonPath
                        ("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath
                ("$.city").value("Sample City"))
                .andExpect(jsonPath(

                "$.course").value("Sample Course"))
                .andExpect(jsonPath
                        ("$.source").value("Sample Source"))
                .andExpect(jsonPath("$.leadStatus").value("STUDENT"))
                .andExpect(jsonPath
                        ("$.paymentInfo[0].date").value("2024-08-23"))
                .andExpect(jsonPath
                        ("$.paymentInfo[0].type").value("tuition"))
                .andExpect(jsonPath("$.paymentInfo[0].amount").value(100.0))
                .andExpect(jsonPath
                        ("$.paymentInfo[0].details")
                        .value("Payment for August semester"));
    }

    private static String asJsonString(Object obj) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}