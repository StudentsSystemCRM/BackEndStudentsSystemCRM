package edutrack.lecturer;

import com.fasterxml.jackson.databind.ObjectMapper;
import edutrack.exception.ResourceNotFoundException;

import edutrack.lecturer.constant.LecturerStatus;
import edutrack.lecturer.controller.LecturerController;
import edutrack.lecturer.dto.request.LecturerCreateRequest;
import edutrack.lecturer.dto.request.LecturerUpdateRequest;
import edutrack.lecturer.dto.response.LecturerDataResponse;

import edutrack.lecturer.service.LecturerService;
import edutrack.security.JwtTokenCreator;
import edutrack.security.JwtTokenValidator;
import edutrack.security.SecurityConfig;
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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LecturerController.class)
@Import({JwtTokenValidator.class, JwtTokenCreator.class, SecurityConfig.class})
@AutoConfigureMockMvc(addFilters = false)
public class LecturerControllerMockTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LecturerService lecturerService;

    @MockBean
    private AccountRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private LecturerCreateRequest createRequest;
    private LecturerUpdateRequest updateRequest;
    private LecturerDataResponse lecturerResponse;

    @BeforeEach
    public void setup() {
        createRequest = new LecturerCreateRequest(
                "John", "Doe", "123456789", "john.doe@example.com",
                "Haifa", LecturerStatus.ACTIVE, new HashSet<>(Set.of("Group1"))
        );

        lecturerResponse = new LecturerDataResponse(
                1L, "John", "Doe", "987654321",
                "john.doe@example.com", "Haifa",
                LecturerStatus.ACTIVE, new HashSet<>(Set.of("Group1"))
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
                .andExpect(jsonPath("$.phoneNumber").value("987654321"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.city").value("Haifa"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.groupNames").isArray())
                .andExpect(jsonPath("$.groupNames[0]").value("Group1"));
        verify(lecturerService, times(1)).createLecturer(any(LecturerCreateRequest.class));
    }
    @Test
    public void createLecturer_ShouldReturnBadRequest_WhenInvalidInput() throws Exception {

        LecturerCreateRequest invalidRequest = new LecturerCreateRequest(
                "",
                "Doe",
                "123456789",
                "john.doe@example.com",
                "Haifa",
                LecturerStatus.ACTIVE,
                new HashSet<>(Set.of("Group1"))
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
                .andExpect(jsonPath("$.phoneNumber").value("987654321"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.city").value("Haifa"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.groupNames").isArray())
                .andExpect(jsonPath("$.groupNames[0]").value("Group1"));

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

        LecturerDataResponse lecturerResponse1 = new LecturerDataResponse(1L, "John", "Doe", "123456789", "john.doe@example.com", "Haifa", LecturerStatus.ACTIVE, Set.of("Group1"));
        LecturerDataResponse lecturerResponse2 = new LecturerDataResponse(2L, "Jane", "Doe", "987654321", "jane.doe@example.com", "Tel Aviv", LecturerStatus.ACTIVE, Set.of("Group2"));

        List<LecturerDataResponse> lecturers = List.of(lecturerResponse1, lecturerResponse2);

        when(lecturerService.findLecturersByLastName(anyString())).thenReturn(lecturers);

        mockMvc.perform(get("/api/lecturers/last-name")
                        .param("lastName", "Doe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))

                .andExpect(jsonPath("$[0].lastName").value("Doe"))

                .andExpect(jsonPath("$[1].lastName").value("Doe"));
    }

    @Test
    public void getLecturersByLastName_ShouldReturnBadRequest_WhenLastNameIsEmpty() throws Exception {
        mockMvc.perform(get("/api/lecturers/last-name")
                        .param("lastName", ""))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getLecturersByStatus_ShouldReturnList() throws Exception {

        LecturerDataResponse lecturerResponse1 = new LecturerDataResponse(1L, "John", "Doe", "123456789", "john.doe@example.com", "Haifa", LecturerStatus.ACTIVE, Set.of("Group1"));
        LecturerDataResponse lecturerResponse2 = new LecturerDataResponse(2L, "Jane", "Doe", "987654321", "jane.doe@example.com", "Tel Aviv", LecturerStatus.ACTIVE, Set.of("Group2"));

        List<LecturerDataResponse> lecturers = List.of(lecturerResponse1, lecturerResponse2);

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
                        .param("status", "INVALID_STATUS"))
                .andExpect(status().isBadRequest());
    }


    @Test
    public void getLecturersByCity_ShouldReturnList() throws Exception {
        LecturerDataResponse lecturerResponse1 = new LecturerDataResponse(1L, "John", "Doe", "123456789", "john.doe@example.com", "Haifa", LecturerStatus.ACTIVE, Set.of("Group1"));
        LecturerDataResponse lecturerResponse2 = new LecturerDataResponse(2L, "Jane", "Doe", "987654321", "jane.doe@example.com", "Haifa", LecturerStatus.ACTIVE, Set.of("Group2"));
        List<LecturerDataResponse> lecturers = List.of(lecturerResponse1, lecturerResponse2);

        when(lecturerService.findLecturersByCity("Haifa")).thenReturn(lecturers);


        mockMvc.perform(get("/api/lecturers/city")
                        .param("city", "Haifa"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].city").value("Haifa"))
                .andExpect(jsonPath("$[1].city").value("Haifa"));
    }

    @Test
    public void getLecturersByCity_ShouldReturnBadRequest_WhenCityIsEmpty() throws Exception {
        mockMvc.perform(get("/api/lecturers/city")
                        .param("city", ""))
                .andExpect(status().isBadRequest());
    }


    @Test
    public void getAllLecturers_ShouldReturnList() throws Exception {
        LecturerDataResponse lecturerResponse1 = new LecturerDataResponse(1L, "John", "Doe", "123456789", "john.doe@example.com", "Haifa", LecturerStatus.ACTIVE, Set.of("Group1"));
        LecturerDataResponse lecturerResponse2 = new LecturerDataResponse(2L, "Jane", "Doe", "987654321", "jane.doe@example.com", "Tel Aviv", LecturerStatus.ACTIVE, Set.of("Group2"));

        List<LecturerDataResponse> lecturers = List.of(lecturerResponse1, lecturerResponse2);

        when(lecturerService.getAllLecturers()).thenReturn(lecturers);

        mockMvc.perform(get("/api/lecturers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2)) // Ожидаем 2 лекторов
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[1].firstName").value("Jane"));
    }
    @Test
    public void getAllLecturers_ShouldReturnEmptyList_WhenNoLecturersFound() throws Exception {
        List<LecturerDataResponse> lecturers = List.of();

        when(lecturerService.getAllLecturers()).thenReturn(lecturers);

        mockMvc.perform(get("/api/lecturers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));
    }

    @Test
    public void updateLecturer_ShouldReturnUpdatedLecturer() throws Exception {
        updateRequest = new LecturerUpdateRequest(
                1L, "John", "Doe", "987654321", "john.doe@example.com",
                "Haifa", LecturerStatus.ACTIVE, new HashSet<>(Set.of("Group1"))
        );

        when(lecturerService.updateLecturer(any(LecturerUpdateRequest.class))).thenReturn(lecturerResponse);

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
                .andExpect(jsonPath("$.groupNames").isArray())
                .andExpect(jsonPath("$.groupNames[0]").value("Group1"));

        verify(lecturerService).updateLecturer(any(LecturerUpdateRequest.class));
    }


    @Test
    public void updateLecturer_ShouldReturnNotFound_WhenLecturerDoesNotExist() throws Exception {
        updateRequest = new LecturerUpdateRequest(
                99L, "John", "Doe", "987654321", "john.doe@example.com",
                "Haifa", LecturerStatus.ACTIVE, new HashSet<>(Set.of("Group1"))
        );
        when(lecturerService.updateLecturer(any(LecturerUpdateRequest.class)))
                .thenThrow(new ResourceNotFoundException("Lecturer not found"));

        mockMvc.perform(put("/api/lecturers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateLecturer_ShouldReturnBadRequest_WhenInvalidInput() throws Exception {

        LecturerUpdateRequest invalidUpdateRequest = new LecturerUpdateRequest(1L,
                "John", "Doe", "invalid-phone", "john.doe@example.com",
                "Haifa", LecturerStatus.ACTIVE, new HashSet<>(Set.of("Group1")));

        mockMvc.perform(put("/api/lecturers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUpdateRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void deleteLecturer_ShouldReturnDeletedLecturer() throws Exception {
        when(lecturerService.deleteLecturer(1L)).thenReturn(lecturerResponse);

        mockMvc.perform(delete("/api/lecturers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.phoneNumber").value("987654321"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.city").value("Haifa"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.groupNames").isArray())
                .andExpect(jsonPath("$.groupNames[0]").value("Group1"));

        verify(lecturerService, times(1)).deleteLecturer(1L);
    }


    @Test
    public void deleteLecturer_ShouldReturnNotFound_WhenLecturerDoesNotExist() throws Exception {

        when(lecturerService.deleteLecturer(99L)).thenThrow(new ResourceNotFoundException("Lecturer not found"));

        mockMvc.perform(delete("/api/lecturers/99"))
                .andExpect(status().isNotFound());

        verify(lecturerService, times(1)).deleteLecturer(99L);

    }




}
