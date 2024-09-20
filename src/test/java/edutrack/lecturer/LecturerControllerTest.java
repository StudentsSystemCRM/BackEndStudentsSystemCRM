package edutrack.lecturer;

import com.fasterxml.jackson.databind.ObjectMapper;
import edutrack.group.repository.GroupRepository;
import edutrack.lecturer.constant.LecturerStatus;
import edutrack.lecturer.controller.LecturerController;
import edutrack.lecturer.dto.request.LecturerCreateRequest;
import edutrack.lecturer.dto.request.LecturerUpdateRequest;
import edutrack.lecturer.dto.response.LecturerDataResponse;
import edutrack.lecturer.repository.LecturerRepository;
import edutrack.lecturer.service.LecturerService;
import edutrack.exception.ResourceNotFoundException;
import edutrack.security.JwtTokenCreator;
import edutrack.security.JwtTokenValidator;
import edutrack.security.SecurityConfig;
import edutrack.user.repository.AccountRepository;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LecturerController.class)
@Import({JwtTokenValidator.class, JwtTokenCreator.class, SecurityConfig.class})
@AutoConfigureMockMvc(addFilters = false)
public class LecturerControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AccountRepository accountRepository;

    @MockBean
    private LecturerService lecturerService;
    @MockBean
    private LecturerRepository lecturerRepository;

    @MockBean
    private GroupRepository groupRepository;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
    }
    @Test
    public void createLecturer_ShouldReturnCreatedLecturer() throws Exception {
        LecturerCreateRequest createRequest = new LecturerCreateRequest("John", "Doe", "123456789", "john.doe@example.com", "New York", LecturerStatus.ACTIVE, Set.of("Group1"));
        LecturerDataResponse response = new LecturerDataResponse(1L, "John", "Doe", "123456789", "john.doe@example.com", "New York", LecturerStatus.ACTIVE, Set.of("Group1"));

        when(lecturerService.createLecturer(any(LecturerCreateRequest.class))).thenReturn(response);

        mockMvc.perform(post("/lecturers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.phoneNumber").value("123456789"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.city").value("New York"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.groupNames[0]").value("Group1"));
    }

    @Test
    public void addGroupsToLecturer_ShouldReturnNoContent() throws Exception {

        doNothing().when(lecturerService).addGroupsToLecturer(anyLong(), any(Set.class));

        mockMvc.perform(post("/lecturers/1/groups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Set.of("Group1", "Group2"))))
                .andExpect(status().isNoContent());


        verify(lecturerService).addGroupsToLecturer(anyLong(), any(Set.class));
    }

    @Test
    public void addGroupsToLecturer_ShouldReturnNotFound_WhenLecturerNotFound() throws Exception {

        doThrow(new ResourceNotFoundException("Lecturer not found")).when(lecturerService)
                .addGroupsToLecturer(anyLong(), any(Set.class));

        mockMvc.perform(post("/lecturers/1/groups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Set.of("Group1", "Group2"))))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Lecturer not found"));
    }

    @Test
    public void addGroupsToLecturer_ShouldReturnInternalServerError_WhenUnexpectedErrorOccurs() throws Exception {

        doThrow(new RuntimeException("Unexpected error")).when(lecturerService)
                .addGroupsToLecturer(anyLong(), any(Set.class));

        mockMvc.perform(post("/lecturers/1/groups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Set.of("Group1", "Group2"))))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("An unexpected error occurred."));
    }

    @Test
    public void getLecturerById_ShouldReturnLecturer() throws Exception {
        LecturerDataResponse response = new LecturerDataResponse(1L, "John", "Doe", "123456789", "john.doe@example.com", "New York", LecturerStatus.ACTIVE, Set.of("Group1"));

        when(lecturerService.getLecturerById(anyLong())).thenReturn(response);

        mockMvc.perform(get("/lecturers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.phoneNumber").value("123456789"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.city").value("New York"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.groupNames[0]").value("Group1"));
    }

    @Test
    public void getLecturersByStatus_ShouldReturnListOfLecturers() throws Exception {
        LecturerDataResponse response = new LecturerDataResponse(1L, "John", "Doe", "123456789", "john.doe@example.com", "New York", LecturerStatus.ACTIVE, Set.of("Group1"));
        List<LecturerDataResponse> responses = List.of(response);

        when(lecturerService.findLecturersByStatus(any(LecturerStatus.class))).thenReturn(responses);

        mockMvc.perform(get("/lecturers/status")
                        .param("status", LecturerStatus.ACTIVE.name()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[0].lastName").value("Doe"))
                .andExpect(jsonPath("$[0].phoneNumber").value("123456789"))
                .andExpect(jsonPath("$[0].email").value("john.doe@example.com"))
                .andExpect(jsonPath("$[0].city").value("New York"))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"))
                .andExpect(jsonPath("$[0].groupNames[0]").value("Group1"));
    }

    @Test
    public void getLecturersByCity_ShouldReturnListOfLecturers() throws Exception {
        LecturerDataResponse response = new LecturerDataResponse(1L, "John", "Doe", "123456789", "john.doe@example.com", "New York", LecturerStatus.ACTIVE, Set.of("Group1"));
        List<LecturerDataResponse> responses = List.of(response);

        when(lecturerService.findLecturersByCity(any(String.class))).thenReturn(responses);

        mockMvc.perform(get("/lecturers/city")
                        .param("city", "New York"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[0].lastName").value("Doe"))
                .andExpect(jsonPath("$[0].phoneNumber").value("123456789"))
                .andExpect(jsonPath("$[0].email").value("john.doe@example.com"))
                .andExpect(jsonPath("$[0].city").value("New York"))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"))
                .andExpect(jsonPath("$[0].groupNames[0]").value("Group1"));
    }

    @Test
    public void getAllLecturers_ShouldReturnListOfLecturers() throws Exception {
        LecturerDataResponse response = new LecturerDataResponse(1L, "John", "Doe", "123456789", "john.doe@example.com", "New York", LecturerStatus.ACTIVE, Set.of("Group1"));
        List<LecturerDataResponse> responses = List.of(response);

        when(lecturerService.getAllLecturers()).thenReturn(responses);

        mockMvc.perform(get("/lecturers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[0].lastName").value("Doe"))
                .andExpect(jsonPath("$[0].phoneNumber").value("123456789"))
                .andExpect(jsonPath("$[0].email").value("john.doe@example.com"))
                .andExpect(jsonPath("$[0].city").value("New York"))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"))
                .andExpect(jsonPath("$[0].groupNames[0]").value("Group1"));
    }

    @Test
    public void updateLecturer_ShouldReturnUpdatedLecturer() throws Exception {
        LecturerUpdateRequest updateRequest = new LecturerUpdateRequest(1L, "John", "Doe", "123456789", "john.doe@example.com", "New York", LecturerStatus.ACTIVE, Set.of("Group1"));
        LecturerDataResponse response = new LecturerDataResponse(1L, "John", "Doe", "123456789", "john.doe@example.com", "New York", LecturerStatus.ACTIVE, Set.of("Group1"));

        when(lecturerService.updateLecturer(any(LecturerUpdateRequest.class))).thenReturn(response);

        mockMvc.perform(put("/lecturers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.phoneNumber").value("123456789"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.city").value("New York"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.groupNames[0]").value("Group1"));
    }

    @Test
    public void deleteLecturer_ShouldReturnDeletedLecturer() throws Exception {
        LecturerDataResponse response = new LecturerDataResponse(1L, "John", "Doe", "123456789", "john.doe@example.com", "New York", LecturerStatus.ACTIVE, Set.of("Group1"));

        when(lecturerService.deleteLecturer(anyLong())).thenReturn(response);

        mockMvc.perform(delete("/lecturers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.phoneNumber").value("123456789"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.city").value("New York"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.groupNames[0]").value("Group1"));
    }

}
