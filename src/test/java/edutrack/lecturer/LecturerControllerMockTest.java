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
        createRequest = new LecturerCreateRequest(
                "John", "Doe", "123456789", "john.doe@example.com", "Haifa",
                LecturerStatus.ACTIVE, Set.of(exampleGroupEntity.getId())
        );

        updateRequest = new LecturerUpdateRequest(
                1L, "John", "Doe", "987654321", "john.doe@example.com", "Haifa",
                LecturerStatus.ACTIVE, Set.of(exampleGroupEntity.getId())
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
                "",
                "Doe",
                "123456789",
                "john.doe@example.com",
                "Haifa",
                LecturerStatus.ACTIVE,
                Set.of(exampleGroupEntity.getId())
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
        LecturerDataResponse lecturerResponse1 = new LecturerDataResponse
                (1L, "John", "Doe", "123456789",
                        "john.doe@example.com", "Haifa", LecturerStatus.ACTIVE,
                        Set.of(groupId1));
        LecturerDataResponse lecturerResponse2 = new LecturerDataResponse(2L,
                "Jane", "Doe", "987654321",
                "jane.doe@example.com", "Tel Aviv", LecturerStatus.ACTIVE,
                Set.of(groupId2));
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
        LecturerDataResponse lecturerResponse1 = new LecturerDataResponse(1L, "John",
                "Doe", "123456789", "john.doe@example.com",
                "Haifa", LecturerStatus.ACTIVE, Set.of(exampleGroupEntity.getId()));
        LecturerDataResponse lecturerResponse2 = new LecturerDataResponse(2L,
                "Jane", "Doe", "987654321",
                "jane.doe@example.com", "Tel Aviv", LecturerStatus.ACTIVE, Set.of(exampleGroupEntity.getId()));
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
        LecturerDataResponse lecturerResponse1 = new LecturerDataResponse(1L, "John", "Doe", "123456789", "john.doe@example.com", "Haifa", LecturerStatus.ACTIVE, Set.of(exampleGroupEntity.getId()));
        LecturerDataResponse lecturerResponse2 = new LecturerDataResponse(2L, "Jane", "Doe", "987654321", "jane.doe@example.com", "Haifa", LecturerStatus.ACTIVE, Set.of(exampleGroupEntity.getId()));
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
        Long groupId1 = 1L;
        Long groupId2 = 2L;

        LecturerDataResponse lecturerResponse1 = new LecturerDataResponse(
                1L, "John", "Doe", "123456789",
                "john.doe@example.com", "Haifa",
                LecturerStatus.ACTIVE, Set.of(groupId1)
        );

        LecturerDataResponse lecturerResponse2 = new LecturerDataResponse(
                2L, "Jane", "Doe", "987654321",
                "jane.doe@example.com", "Tel Aviv",
                LecturerStatus.ACTIVE, Set.of(groupId2)
        );

        List<LecturerDataResponse> lecturers = List.of(lecturerResponse1, lecturerResponse2);

        when(lecturerService.getAllLecturers()).thenReturn(lecturers);

        mockMvc.perform(get("/api/lecturers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[0].lastName").value("Doe"))
                .andExpect(jsonPath("$[0].phoneNumber").value("123456789"))
                .andExpect(jsonPath("$[0].email").value("john.doe@example.com"))
                .andExpect(jsonPath("$[0].city").value("Haifa"))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"))
                .andExpect(jsonPath("$[0].groupIds").isArray())
                .andExpect(jsonPath("$[0].groupIds[0]").value(1L)) // Исправлено здесь
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].firstName").value("Jane"))
                .andExpect(jsonPath("$[1].lastName").value("Doe"))
                .andExpect(jsonPath("$[1].phoneNumber").value("987654321"))
                .andExpect(jsonPath("$[1].email").value("jane.doe@example.com"))
                .andExpect(jsonPath("$[1].city").value("Tel Aviv"))
                .andExpect(jsonPath("$[1].status").value("ACTIVE"))
                .andExpect(jsonPath("$[1].groupIds").isArray())
                .andExpect(jsonPath("$[1].groupIds[0]").value(2L));
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
        LecturerDataResponse initialLecturer = new LecturerDataResponse(
                1L, "John", "Doe", "123456789",
                "john.doe@example.com", "Haifa",
                LecturerStatus.ACTIVE, Set.of(exampleGroupEntity.getId())
        );

        when(lecturerService.createLecturer(any(LecturerCreateRequest.class))).thenReturn(lecturerResponse);

        lecturerService.createLecturer(createRequest);

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
                .andExpect(jsonPath("$.groupIds[0]").value(exampleGroupEntity.getId())); // Проверка идентификатора группы

        verify(lecturerService).updateLecturer(any(LecturerUpdateRequest.class));
    }
    @Test
    public void updateLecturer_ShouldReturnNotFound_WhenLecturerDoesNotExist() throws Exception {

        updateRequest = new LecturerUpdateRequest(
                99L, "John", "Doe", "987654321", "john.doe@example.com",
                "Haifa", LecturerStatus.ACTIVE, Set.of(exampleGroupEntity.getId())
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
        LecturerUpdateRequest invalidUpdateRequest = new LecturerUpdateRequest(
                1L, "John", "Doe", "invalid-phone", "john.doe@example.com",
                "Haifa", LecturerStatus.ACTIVE, Set.of(exampleGroupEntity.getId())
        );
        mockMvc.perform(put("/api/lecturers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUpdateRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteLecturer_ShouldReturnDeletedLecturer() throws Exception {
        LecturerDataResponse initialLecturer = new LecturerDataResponse(
                1L, "John", "Doe", "123456789",
                "john.doe@example.com", "Haifa",
                LecturerStatus.ACTIVE, Set.of(exampleGroupEntity.getId())
        );
        when(lecturerService.deleteLecturer(initialLecturer.getId())).thenReturn(initialLecturer);
        mockMvc.perform(delete("/api/lecturers/{id}", initialLecturer.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(initialLecturer.getId()))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.phoneNumber").value("123456789"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.city").value("Haifa"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.groupIds").isArray())
                .andExpect(jsonPath("$.groupIds[0]").value(exampleGroupEntity.getId())); // Проверка идентификатора группы
        verify(lecturerService, times(1)).deleteLecturer(initialLecturer.getId());
    }
    @Test
    public void deleteLecturer_ShouldReturnNotFound_WhenLecturerDoesNotExist() throws Exception {
        Long notExistLecturerId = 99L;
        when(lecturerService.deleteLecturer(notExistLecturerId))
                .thenThrow(new ResourceNotFoundException("Lecturer not found"));
        mockMvc.perform(delete("/api/lecturers/{id}", notExistLecturerId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Lecturer not found")); // Ожидаем сообщение об ошибке
        verify(lecturerService, times(1)).deleteLecturer(notExistLecturerId);
    }
}