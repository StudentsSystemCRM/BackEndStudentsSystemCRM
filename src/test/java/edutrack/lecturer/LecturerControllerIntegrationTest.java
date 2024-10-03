//package edutrack.lecturer;
//
//import edutrack.lecturer.constant.LecturerStatus;
//import edutrack.lecturer.controller.LecturerController;
//import edutrack.lecturer.dto.request.LecturerCreateRequest;
//import edutrack.lecturer.dto.request.LecturerUpdateRequest;
//import edutrack.lecturer.dto.response.LecturerDataResponse;
//import edutrack.lecturer.entity.LecturerEntity;
//import edutrack.lecturer.repository.LecturerRepository;
//import edutrack.lecturer.service.LecturerService;
//import edutrack.security.JwtTokenCreator;
//import edutrack.security.JwtTokenValidator;
//import edutrack.security.SecurityConfig;
//import edutrack.user.repository.AccountRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.context.annotation.Import;
//import org.springframework.http.MediaType;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.util.*;
//
//import static org.hamcrest.Matchers.is;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(LecturerController.class)
//@AutoConfigureMockMvc(addFilters = false)
//@Import({JwtTokenValidator.class, JwtTokenCreator.class, SecurityConfig.class})
//class LecturerControllerIntegrationTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//    @MockBean
//    LecturerService lecturerService;
//
//
//    @Autowired
//    private LecturerRepository lecturerRepository;
//
//    private LecturerCreateRequest lecturerCreateRequest;
//    @MockBean
//    private AccountRepository userRepository;
//
//
//    private LecturerCreateRequest requestGroup = new LecturerCreateRequest();
//    private LecturerDataResponse responseGroup = new LecturerDataResponse();
//    @BeforeEach
//    void setUp() {
//        Set<String> groupNames = new HashSet<String>();
//        groupNames.add("group1");
//        lecturerRepository.deleteAll();
//        lecturerCreateRequest = new LecturerCreateRequest();
//        lecturerCreateRequest.setFirstName("John");
//        lecturerCreateRequest.setLastName("Doe");
//        lecturerCreateRequest.setPhoneNumber("+123456789");
//        lecturerCreateRequest.setEmail("john.doe@example.com");
//        lecturerCreateRequest.setCity("CityName");
//        lecturerCreateRequest.setStatus(LecturerStatus.ACTIVE);
//        lecturerCreateRequest.setGroups(groupNames);
//    }
//
//    @Test
//    @WithMockUser
//    void shouldCreateLecturer() throws Exception {
//        mockMvc.perform(post("/lecturers")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{\"firstName\":\"John\",\"lastName\":\"Doe\",\"phoneNumber\":\"+123456789\",\"email\":\"john.doe@example.com\",\"city\":\"CityName\",\"status\":\"ACTIVE\"}"))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.firstName", is("John")))
//                .andExpect(jsonPath("$.lastName", is("Doe")))
//                .andExpect(jsonPath("$.email", is("john.doe@example.com")));
//    }
//
//    @Test
//    @WithMockUser
//    void shouldGetLecturerById() throws Exception {
//        LecturerDataResponse savedLecturer = lecturerRepository.save(new LecturerEntity(lecturerCreateRequest));
//
//        mockMvc.perform(get("/lecturers/" + savedLecturer.getId()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id", is(savedLecturer.getId().intValue())))
//                .andExpect(jsonPath("$.firstName", is("John")))
//                .andExpect(jsonPath("$.lastName", is("Doe")));
//    }
//
//    @Test
//    @WithMockUser
//    void shouldReturnNotFoundWhenLecturerNotFound() throws Exception {
//        mockMvc.perform(get("/lecturers/999"))
//                .andExpect(status().isNotFound())
//                .andExpect(content().string("Lecturer not found"));
//    }
//
//    @Test
//    @WithMockUser
//    void shouldGetAllLecturers() throws Exception {
//        // Создаем лектора и сохраняем в базе
//        lecturerRepository.save(new LecturerEntity(lecturerCreateRequest));
//
//        mockMvc.perform(get("/lecturers"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].firstName", is("John")))
//                .andExpect(jsonPath("$[0].lastName", is("Doe")));
//    }
//
//    @Test
//    @WithMockUser
//    void shouldDeleteLecturer() throws Exception {
//        // Создаем лектора и сохраняем в базе
//        LecturerDataResponse savedLecturer = lecturerRepository.save(new Lecturer(lecturerCreateRequest));
//
//        mockMvc.perform(delete("/lecturers/" + savedLecturer.getId()))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    @WithMockUser
//    void shouldUpdateLecturer() throws Exception {
//
//        LecturerDataResponse savedLecturer = lecturerRepository.save(new Lecturer(lecturerCreateRequest));
//
//        LecturerUpdateRequest updateRequest = new LecturerUpdateRequest();
//        updateRequest.setFirstName("UpdatedName");
//        updateRequest.setLastName("UpdatedLastName");
//
//        mockMvc.perform(put("/lecturers/" + savedLecturer.getId())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{\"firstName\":\"UpdatedName\",\"lastName\":\"UpdatedLastName\"}"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id", is(savedLecturer.getId().intValue())))
//                .andExpect(jsonPath("$.firstName", is("UpdatedName")))
//                .andExpect(jsonPath("$.lastName", is("UpdatedLastName")));
//    }
//}
