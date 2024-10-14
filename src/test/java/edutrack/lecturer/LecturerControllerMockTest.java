package edutrack.lecturer;

import com.fasterxml.jackson.databind.ObjectMapper;
import edutrack.exception.ResourceNotFoundException;
import edutrack.group.constant.GroupStatus;
import edutrack.group.entity.GroupEntity;
import edutrack.lecturer.constant.LecturerStatus;
import edutrack.lecturer.controller.LecturerController;
import edutrack.lecturer.dto.request.LecturerCreateRequest;
import edutrack.lecturer.dto.request.LecturerUpdateRequest;
import edutrack.lecturer.dto.response.LecturerDataResponse;
import edutrack.lecturer.repository.LecturerRepository;
import edutrack.lecturer.service.LecturerService;
import edutrack.security.WebSecurityConfig;
import edutrack.security.jwt.JwtTokenProvider;
import edutrack.user.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LecturerController.class)
@Import({JwtTokenProvider.class, WebSecurityConfig.class})
@AutoConfigureMockMvc(addFilters = false)
public class LecturerControllerMockTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LecturerService lecturerService;

    @MockBean
    private LecturerRepository lecturerRepository;

    @MockBean
    private AccountRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private GroupEntity exampleGroupEntity;
    private LecturerCreateRequest createRequest;
    private LecturerUpdateRequest updateRequest;
    private LecturerDataResponse lecturerResponse;
    private LecturerDataResponse lecturerUpdateResponse;

    @BeforeEach
    public void setup() {
        exampleGroupEntity = new GroupEntity(
                1L, "Example Group", "example-whatsapp", "example-skype", "example-slack",
                GroupStatus.ACTIVE, LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31),
                LocalDate.of(2024, 6, 30), new ArrayList<>(), new ArrayList<>(),
                new ArrayList<>(), new ArrayList<>(), null, null
        );

        createRequest = new LecturerCreateRequest(
                "John", "Doe", "123456789", "john.doe@example.com", "Haifa",
                LecturerStatus.ACTIVE, Set.of(exampleGroupEntity.getId())
        );

        updateRequest = new LecturerUpdateRequest(
                1L, "John", "Doe", "987654321", "john.doe@example.com", "Haifa",
                LecturerStatus.ACTIVE, Set.of(exampleGroupEntity.getId())
        );

        lecturerResponse = new LecturerDataResponse(
                1L, "John", "Doe", "123456789",
                "john.doe@example.com", "Haifa", LecturerStatus.ACTIVE, Set.of(exampleGroupEntity.getId())
        );

        lecturerUpdateResponse = new LecturerDataResponse(
                1L, "John", "Doe", "987654321",
                "john.doe@example.com", "Haifa", LecturerStatus.ACTIVE, Set.of(exampleGroupEntity.getId())
        );
    }

    @Test
    public void createLecturer_ShouldReturnCreatedLecturer() throws Exception {
        when(lecturerService.createLecturer(any(LecturerCreateRequest.class))).thenReturn(lecturerResponse);
        mockMvc.perform(post("/api/lecturers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.phoneNumber").value("123456789"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.city").value("Haifa"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.groupIds").isArray())
                .andExpect(jsonPath("$.groupIds[0]").value(1L));

        verify(lecturerService, times(1)).createLecturer(any(LecturerCreateRequest.class));
    }

    @Test
    public void createLecturer_ShouldReturnBadRequest_WhenInvalidInput() throws Exception {
        LecturerCreateRequest invalidRequest = new LecturerCreateRequest(
                "", "Doe", "123456789", "john.doe@example.com", "Haifa",
                LecturerStatus.ACTIVE, Set.of(exampleGroupEntity.getId())
        );
        mockMvc.perform(post("/api/lecturers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getLecturerById_ShouldReturnLecturer() throws Exception {
        when(lecturerService.getLecturerById(1L)).thenReturn(lecturerResponse);
        mockMvc.perform(get("/api/lecturers/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.phoneNumber").value("123456789"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.city").value("Haifa"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.groupIds").isArray())
                .andExpect(jsonPath("$.groupIds[0]").value(1L));

        verify(lecturerService, times(1)).getLecturerById(1L);
    }

    @Test
    public void getLecturerById_ShouldReturnNotFound_WhenLecturerDoesNotExist() throws Exception {
        when(lecturerService.getLecturerById(99L)).thenThrow(new ResourceNotFoundException("Lecturer not found"));
        mockMvc.perform(get("/api/lecturers/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(lecturerService, times(1)).getLecturerById(99L);
    }

    @Test
    public void getLecturersByLastName_ShouldReturnList() throws Exception {
        Long groupId1 = 1L;
        Long groupId2 = 2L;

        List<LecturerDataResponse> lecturers = createLecturerList(groupId1, groupId2);
        when(lecturerService.findLecturersByLastName(anyString())).thenReturn(lecturers);

        mockMvc.perform(get("/api/lecturers/last-name")
                        .param("lastName", "Doe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].lastName").value("Doe"))
                .andExpect(jsonPath("$[1].lastName").value("Doe"));
    }

    private List<LecturerDataResponse> createLecturerList(Long groupId1, Long groupId2) {
        LecturerDataResponse lecturerResponse1 = new LecturerDataResponse(1L, "John", "Doe", "123456789",
                "john.doe@example.com", "Haifa", LecturerStatus.ACTIVE, Set.of(groupId1));
        LecturerDataResponse lecturerResponse2 = new LecturerDataResponse(2L, "Jane", "Doe", "987654321",
                "jane.doe@example.com", "Tel Aviv", LecturerStatus.ACTIVE, Set.of(groupId2));
        return List.of(lecturerResponse1, lecturerResponse2);
    }

    @Test
    public void getLecturersByLastName_ShouldReturnBadRequest_WhenLastNameIsEmpty() throws Exception {
        mockMvc.perform(get("/api/lecturers/last-name")
                        .param("lastName", ""))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getLecturersByStatus_ShouldReturnList() throws Exception {
        List<LecturerDataResponse> lecturers = createLecturerList(1L, 2L);
        when(lecturerService.findLecturersByStatus(LecturerStatus.ACTIVE)).thenReturn(lecturers);

        mockMvc.perform(get("/api/lecturers/status")
                        .param("status", "ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"))
                .andExpect(jsonPath("$[1].status").value("ACTIVE"));
    }

    @Test
    public void getLecturersByStatus_ShouldReturnBadRequest_WhenStatusIsInvalid() throws Exception {
        mockMvc.perform(get("/api/lecturers/status")
                        .param("status", "INVALID"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateLecturer_ShouldReturnUpdatedLecturer() throws Exception {
        when(lecturerService.updateLecturer(any(LecturerUpdateRequest.class))).thenReturn(lecturerUpdateResponse);

        mockMvc.perform(put("/api/lecturers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.phoneNumber").value("987654321"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.city").value("Haifa"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.groupIds").isArray())
                .andExpect(jsonPath("$.groupIds[0]").value(1L));

        verify(lecturerService, times(1)).updateLecturer(any(LecturerUpdateRequest.class));
    }


    @Test
    public void updateLecturer_ShouldReturnBadRequest_WhenInvalidInput() throws Exception {

        LecturerUpdateRequest invalidUpdateRequest = new LecturerUpdateRequest(
                1L, "", "Doe", "invalid-phone", "invalid-email", "InvalidCity",
                LecturerStatus.ACTIVE, Set.of(exampleGroupEntity.getId())
        );

        mockMvc.perform(put("/api/lecturers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUpdateRequest)))
                .andExpect(status().isBadRequest());
    }
}
