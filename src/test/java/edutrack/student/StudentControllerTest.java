package edutrack.student;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import edutrack.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import edutrack.student.constant.LeadStatus;
import edutrack.student.controller.StudentController;
import edutrack.student.dto.request.StudentCreateRequest;
import edutrack.student.dto.response.StudentDataResponse;
import edutrack.student.service.StudentService;
import edutrack.user.repository.AccountRepository;
import edutrack.security.WebSecurityConfig;

@WebMvcTest(StudentController.class)
@Import({ JwtTokenProvider.class, WebSecurityConfig.class })
@AutoConfigureMockMvc(addFilters = false)
public class StudentControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private StudentService studentService;

	@MockBean
	private AccountRepository userRepository;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void testGetAllStudents() throws Exception {
		mockMvc.perform(get("/api/students")).andExpect(status().isOk());
	}

	@Test
	public void testGetStudentById() throws Exception {
		Long studentId = 1L;
		StudentDataResponse mockResponse = new StudentDataResponse(studentId, "John", "Doe", "1234567890",
				"john.doe@example.com", "City", "Course", "Source", LeadStatus.IN_WORK);
		Mockito.when(studentService.getStudentById(studentId)).thenReturn(mockResponse);

		mockMvc.perform(get("/api/students/{id}", studentId)).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(studentId)).andExpect(jsonPath("$.name").value("John"))
				.andExpect(jsonPath("$.surname").value("Doe"));
	}

	@Test
	public void testCreateStudent() throws Exception {
		StudentCreateRequest request = new StudentCreateRequest("John", "Doe", "1234567890", "john.doe@example.com",
				"City", "Course", "Source", LeadStatus.STUDENT, "Comment");
		StudentDataResponse response = new StudentDataResponse();
		response.setId(1L);

		Mockito.when(studentService.createStudent(any(StudentCreateRequest.class))).thenReturn(response);

		mockMvc.perform(
				post("/api/students/create_student").contentType("application/json").content(asJsonString(request)))
				.andExpect(status().isOk()).andExpect(jsonPath("$.id").value(response.getId()));
	}

	@Test
	public void testGetStudentsByName() throws Exception {
		String name = "John";
		List<StudentDataResponse> mockResponse = new ArrayList<>();
		mockResponse.add(new StudentDataResponse(1L, name, "Doe", "1234567890", "john.doe@example.com", "New York",
				"Engineering", "Website", LeadStatus.STUDENT));
		mockResponse.add(new StudentDataResponse(2L, name, "Smith", "0987654321", "john.smith@example.com",
				"Los Angeles", "Mathematics", "Referral", LeadStatus.IN_WORK));

		Mockito.when(studentService.getStudentsByName(eq(name))).thenReturn(mockResponse);

		mockMvc.perform(get("/api/students/name").param("name", name)).andExpect(status().isOk())
				.andExpect(jsonPath("$[0].name").value(name)).andExpect(jsonPath("$[1].name").value(name))
				.andExpect(jsonPath("$[0].surname").value("Doe")).andExpect(jsonPath("$[1].surname").value("Smith"));
	}

	@Test
	public void testGetStudentsByName_NoMatches() throws Exception {
		String name = "NonexistentName";
		List<StudentDataResponse> mockResponse = new ArrayList<>();

		Mockito.when(studentService.getStudentsByName(eq(name))).thenReturn(mockResponse);

		mockMvc.perform(get("/api/students/name").param("name", name)).andExpect(status().isOk())
				.andExpect(jsonPath("$").isEmpty());
	}

	@Test
	public void testGetStudentsBySurname() throws Exception {
		String surname = "Doe";
		List<StudentDataResponse> mockResponse = new ArrayList<>();
		mockResponse.add(new StudentDataResponse(1L, "John", surname, "1234567890", "john.doe@example.com", "New York",
				"Engineering", "Website", LeadStatus.STUDENT));
		mockResponse.add(new StudentDataResponse(2L, "Jane", surname, "0987654321", "jane.doe@example.com", "New York",
				"Engineering", "Website", LeadStatus.CONSULTATION));

		Mockito.when(studentService.getStudentsBySurname(eq(surname))).thenReturn(mockResponse);

		mockMvc.perform(get("/api/students/surname").param("surname", surname)).andExpect(status().isOk())
				.andExpect(jsonPath("$[0].surname").value(surname)).andExpect(jsonPath("$[1].surname").value(surname));
	}

	@Test
	public void testGetStudentsByNameAndSurname() throws Exception {
		String name = "John";
		String surname = "Doe";
		List<StudentDataResponse> mockResponse = new ArrayList<>();
		mockResponse.add(new StudentDataResponse(1L, name, surname, "1234567890", "john.doe@example.com", "New York",
				"Engineering", "Website", LeadStatus.CONSULTATION));
		mockResponse.add(new StudentDataResponse(2L, name, surname, "0987654321", "john.doe@example.com", "New York",
				"Engineering", "Website", LeadStatus.SAVE_FOR_LATER));

		Mockito.when(studentService.getStudentsByNameAndSurname(eq(name), eq(surname))).thenReturn(mockResponse);

		mockMvc.perform(get("/api/students/name_and_surname").param("name", name).param("surname", surname))
				.andExpect(status().isOk()).andExpect(jsonPath("$[0].name").value(name))
				.andExpect(jsonPath("$[0].surname").value(surname)).andExpect(jsonPath("$[1].name").value(name))
				.andExpect(jsonPath("$[1].surname").value(surname));
	}

	@Test
	public void testUpdateStudent() throws Exception {
		mockMvc.perform(put("/api/students/update_student_information").contentType("application/json")
				.content("{\"id\":1,\"name\":\"John\",\"surname\":\"Doe\"}")).andExpect(status().isOk());
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