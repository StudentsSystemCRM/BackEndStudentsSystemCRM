package edutrack.lecturer;

import edutrack.exception.ResourceNotFoundException;
import edutrack.group.constant.GroupStatus;
import edutrack.group.entity.GroupEntity;
import edutrack.group.repository.GroupRepository;
import edutrack.lecturer.constant.LecturerStatus;
import edutrack.lecturer.dto.request.LecturerCreateRequest;
import edutrack.lecturer.dto.request.LecturerUpdateRequest;
import edutrack.lecturer.dto.response.LecturerDataResponse;
import edutrack.lecturer.entity.LecturerEntity;
import edutrack.lecturer.repository.LecturerRepository;
import edutrack.lecturer.service.LecturerServiceImpl;
import edutrack.student.exception.EmailAlreadyInUseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LecturerServiceImplMockTest {

    @Mock
    private LecturerRepository lecturerRepo;
    @Mock
    private GroupRepository groupRepo;
    @InjectMocks
    private LecturerServiceImpl lecturerService;
    private LecturerEntity lecturer;
    private LecturerCreateRequest createRequest;
    private LecturerUpdateRequest updateRequest;
    private GroupEntity exampleGroupEntity;

    @BeforeEach
    void setUp() {
        exampleGroupEntity = new GroupEntity(1L, "Example Group", "example-whatsapp", "example-skype", "example-slack", GroupStatus.ACTIVE, LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31), LocalDate.of(2024, 6, 30), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), null, null);

        lecturer = new LecturerEntity(1L, "John", "Doe", "123456789", "john.doe@example.com", "Haifa", LecturerStatus.ACTIVE, Set.of(exampleGroupEntity), null, null);

        createRequest = new LecturerCreateRequest("John", "Doe", "123456789", "john.doe@example.com", "Haifa", LecturerStatus.ACTIVE, Set.of(exampleGroupEntity.getId()));

        updateRequest = new LecturerUpdateRequest(1L, "John", "Doe", "987654321", "john.doe@example.com", "Haifa", LecturerStatus.ACTIVE, Set.of(exampleGroupEntity.getId()));
    }

    @Test
    void testGetLecturerById_Success() {
        Long lecturerId = 1L;
        when(lecturerRepo.findById(lecturerId)).thenReturn(Optional.of(lecturer));
        LecturerDataResponse response = lecturerService.getLecturerById(lecturerId);
        assertNotNull(response);
        assertEquals(lecturer.getId(), response.getId());
        assertEquals(lecturer.getFirstName(), response.getFirstName());
        assertEquals(lecturer.getLastName(), response.getLastName());
        assertEquals(lecturer.getPhoneNumber(), response.getPhoneNumber());
        assertEquals(lecturer.getEmail(), response.getEmail());
        assertEquals(lecturer.getCity(), response.getCity());
        assertEquals(lecturer.getStatus(), response.getStatus());
        assertEquals(1, response.getGroupIds().size());
        assertTrue(response.getGroupIds().contains(exampleGroupEntity.getId()));
    }

    @Test
    void testGetLecturerById_NotFound() {
        Long lecturerId = 1L;
        when(lecturerRepo.findById(lecturerId)).thenReturn(Optional.empty());
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> lecturerService.getLecturerById(lecturerId));
        assertEquals("Lecturer not found with id: " + lecturerId, exception.getMessage());
    }

    @Test
    void testFindLecturersByLastName_Success() {
        String lastName = "Doe";
        LecturerEntity secondLecturer = new LecturerEntity(2L, "Jane", "Doe", "987654321", "jane.doe@example.com", "Tel Aviv", LecturerStatus.ACTIVE, Set.of(exampleGroupEntity), null, null);
        when(lecturerRepo.findByLastName(lastName)).thenReturn(List.of(lecturer, secondLecturer));
        List<LecturerDataResponse> responses = lecturerService.findLecturersByLastName(lastName);
        assertNotNull(responses);
        assertEquals(2, responses.size());
        LecturerDataResponse firstResponse = responses.get(0);
        assertEquals(lecturer.getId(), firstResponse.getId());
        assertEquals(lecturer.getFirstName(), firstResponse.getFirstName());
        assertEquals(lecturer.getLastName(), firstResponse.getLastName());
        assertEquals(lecturer.getPhoneNumber(), firstResponse.getPhoneNumber());
        assertEquals(lecturer.getEmail(), firstResponse.getEmail());
        assertEquals(lecturer.getCity(), firstResponse.getCity());
        assertEquals(lecturer.getStatus(), firstResponse.getStatus());
        assertEquals(1, firstResponse.getGroupIds().size());
        assertTrue(firstResponse.getGroupIds().contains(exampleGroupEntity.getId()));
        LecturerDataResponse secondResponse = responses.get(1);
        assertEquals(secondLecturer.getId(), secondResponse.getId());
        assertEquals(secondLecturer.getFirstName(), secondResponse.getFirstName());
        assertEquals(secondLecturer.getLastName(), secondResponse.getLastName());
        assertEquals(secondLecturer.getPhoneNumber(), secondResponse.getPhoneNumber());
        assertEquals(secondLecturer.getEmail(), secondResponse.getEmail());
        assertEquals(secondLecturer.getCity(), secondResponse.getCity());
        assertEquals(secondLecturer.getStatus(), secondResponse.getStatus());
        assertEquals(1, secondResponse.getGroupIds().size());
        assertTrue(secondResponse.getGroupIds().contains(exampleGroupEntity.getId()));
    }

    @Test
    void testFindLecturersByLastName_NotFound() {
        String lastName = "Smith";
        when(lecturerRepo.findByLastName(lastName)).thenReturn(Collections.emptyList());
        List<LecturerDataResponse> responses = lecturerService.findLecturersByLastName(lastName);
        assertNotNull(responses);
        assertTrue(responses.isEmpty());
    }

    @Test
    void testFindLecturersByStatus_Success() {
        LecturerStatus status = LecturerStatus.ACTIVE;
        LecturerEntity secondLecturer = new LecturerEntity(2L, "Jane", "Doe", "987654321", "jane.doe@example.com", "Tel Aviv", status, Set.of(exampleGroupEntity), null, null);
        when(lecturerRepo.findByStatus(status)).thenReturn(List.of(lecturer, secondLecturer));
        List<LecturerDataResponse> responses = lecturerService.findLecturersByStatus(status);
        assertNotNull(responses);
        assertEquals(2, responses.size());
        LecturerDataResponse firstResponse = responses.get(0);
        assertEquals(lecturer.getId(), firstResponse.getId());
        assertEquals(lecturer.getFirstName(), firstResponse.getFirstName());
        assertEquals(lecturer.getLastName(), firstResponse.getLastName());
        assertEquals(lecturer.getPhoneNumber(), firstResponse.getPhoneNumber());
        assertEquals(lecturer.getEmail(), firstResponse.getEmail());
        assertEquals(lecturer.getCity(), firstResponse.getCity());
        assertEquals(lecturer.getStatus(), firstResponse.getStatus());
        assertEquals(1, firstResponse.getGroupIds().size());
        assertTrue(firstResponse.getGroupIds().contains(exampleGroupEntity.getId()));
        LecturerDataResponse secondResponse = responses.get(1);
        assertEquals(secondLecturer.getId(), secondResponse.getId());
        assertEquals(secondLecturer.getFirstName(), secondResponse.getFirstName());
        assertEquals(secondLecturer.getLastName(), secondResponse.getLastName());
        assertEquals(secondLecturer.getPhoneNumber(), secondResponse.getPhoneNumber());
        assertEquals(secondLecturer.getEmail(), secondResponse.getEmail());
        assertEquals(secondLecturer.getCity(), secondResponse.getCity());
        assertEquals(secondLecturer.getStatus(), secondResponse.getStatus());
        assertEquals(1, secondResponse.getGroupIds().size());
        assertTrue(secondResponse.getGroupIds().contains(exampleGroupEntity.getId()));
    }

    @Test
    void testFindLecturersByStatus_NotFound() {
        LecturerStatus status = LecturerStatus.INACTIVE;
        when(lecturerRepo.findByStatus(status)).thenReturn(Collections.emptyList());
        List<LecturerDataResponse> responses = lecturerService.findLecturersByStatus(status);
        assertNotNull(responses);
        assertTrue(responses.isEmpty());
    }

    @Test
    void testFindLecturersByCity_Success() {
        String city = "Haifa";
        LecturerEntity secondLecturer = new LecturerEntity(2L, "Jane", "Doe", "987654321", "jane.doe@example.com", city, LecturerStatus.ACTIVE, Set.of(exampleGroupEntity), null, null);
        when(lecturerRepo.findByCity(city)).thenReturn(List.of(lecturer, secondLecturer));
        List<LecturerDataResponse> responses = lecturerService.findLecturersByCity(city);
        assertNotNull(responses);
        assertEquals(2, responses.size());
        LecturerDataResponse firstResponse = responses.get(0);
        assertEquals(lecturer.getId(), firstResponse.getId());
        assertEquals(lecturer.getFirstName(), firstResponse.getFirstName());
        assertEquals(lecturer.getLastName(), firstResponse.getLastName());
        assertEquals(lecturer.getPhoneNumber(), firstResponse.getPhoneNumber());
        assertEquals(lecturer.getEmail(), firstResponse.getEmail());
        assertEquals(lecturer.getCity(), firstResponse.getCity());
        assertEquals(lecturer.getStatus(), firstResponse.getStatus());
        assertEquals(1, firstResponse.getGroupIds().size());
        assertTrue(firstResponse.getGroupIds().contains(exampleGroupEntity.getId()));
        LecturerDataResponse secondResponse = responses.get(1);
        assertEquals(secondLecturer.getId(), secondResponse.getId());
        assertEquals(secondLecturer.getFirstName(), secondResponse.getFirstName());
        assertEquals(secondLecturer.getLastName(), secondResponse.getLastName());
        assertEquals(secondLecturer.getPhoneNumber(), secondResponse.getPhoneNumber());
        assertEquals(secondLecturer.getEmail(), secondResponse.getEmail());
        assertEquals(secondLecturer.getCity(), secondResponse.getCity());
        assertEquals(secondLecturer.getStatus(), secondResponse.getStatus());
        assertEquals(1, secondResponse.getGroupIds().size());
        assertTrue(secondResponse.getGroupIds().contains(exampleGroupEntity.getId()));
    }

    @Test
    void testFindLecturersByCity_NotFound() {
        String city = "Unknown City";
        when(lecturerRepo.findByCity(city)).thenReturn(Collections.emptyList());
        List<LecturerDataResponse> responses = lecturerService.findLecturersByCity(city);
        assertNotNull(responses);
        assertTrue(responses.isEmpty());
    }

    @Test
    void testGetAllLecturers_Success() {

        LecturerEntity secondLecturer = new LecturerEntity(2L, "Jane", "Doe", "987654321", "jane.doe@example.com", "Tel Aviv", LecturerStatus.ACTIVE, Set.of(exampleGroupEntity), null, null);
        when(lecturerRepo.findAll()).thenReturn(List.of(lecturer, secondLecturer));
        List<LecturerDataResponse> responses = lecturerService.getAllLecturers();
        assertNotNull(responses);
        assertEquals(2, responses.size());
        LecturerDataResponse firstResponse = responses.get(0);
        assertEquals(lecturer.getId(), firstResponse.getId());
        assertEquals(lecturer.getFirstName(), firstResponse.getFirstName());
        assertEquals(lecturer.getLastName(), firstResponse.getLastName());
        assertEquals(lecturer.getPhoneNumber(), firstResponse.getPhoneNumber());
        assertEquals(lecturer.getEmail(), firstResponse.getEmail());
        assertEquals(lecturer.getCity(), firstResponse.getCity());
        assertEquals(lecturer.getStatus(), firstResponse.getStatus());
        assertEquals(1, firstResponse.getGroupIds().size());
        assertTrue(firstResponse.getGroupIds().contains(exampleGroupEntity.getId()));
        LecturerDataResponse secondResponse = responses.get(1);
        assertEquals(secondLecturer.getId(), secondResponse.getId());
        assertEquals(secondLecturer.getFirstName(), secondResponse.getFirstName());
        assertEquals(secondLecturer.getLastName(), secondResponse.getLastName());
        assertEquals(secondLecturer.getPhoneNumber(), secondResponse.getPhoneNumber());
        assertEquals(secondLecturer.getEmail(), secondResponse.getEmail());
        assertEquals(secondLecturer.getCity(), secondResponse.getCity());
        assertEquals(secondLecturer.getStatus(), secondResponse.getStatus());
        assertEquals(1, secondResponse.getGroupIds().size());
        assertTrue(secondResponse.getGroupIds().contains(exampleGroupEntity.getId()));
    }

    @Test
    void testGetAllLecturers_EmptyList() {

        when(lecturerRepo.findAll()).thenReturn(Collections.emptyList());
        List<LecturerDataResponse> responses = lecturerService.getAllLecturers();
        assertNotNull(responses);
        assertTrue(responses.isEmpty());
    }
    @Test
    void testCreateLecturer_Success() {

        when(lecturerRepo.existsByEmail(createRequest.getEmail())).thenReturn(false);
        when(groupRepo.findAllById(createRequest.getGroupIds())).thenReturn(List.of(exampleGroupEntity));
        when(lecturerRepo.save(any(LecturerEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        LecturerDataResponse response = lecturerService.createLecturer(createRequest);
        assertNotNull(response);
        assertEquals(createRequest.getEmail(), response.getEmail());
        assertEquals(createRequest.getFirstName(), response.getFirstName());
        assertEquals(createRequest.getLastName(), response.getLastName());
        assertEquals(createRequest.getPhoneNumber(), response.getPhoneNumber());
        assertEquals(createRequest.getCity(), response.getCity());
        assertEquals(createRequest.getStatus(), response.getStatus());
        assertEquals(1, response.getGroupIds().size());
        assertTrue(response.getGroupIds().contains(exampleGroupEntity.getId()));
    }


    @Test
    void testCreateLecturer_EmailAlreadyInUse() {

        when(lecturerRepo.existsByEmail(createRequest.getEmail())).thenReturn(true);
        EmailAlreadyInUseException exception = assertThrows(EmailAlreadyInUseException.class, () -> lecturerService.createLecturer(createRequest));
        assertEquals("Email already in use: " + createRequest.getEmail(), exception.getMessage());
    }

    @Test
    void testUpdateLecturer_Success() {
        when(lecturerRepo.findById(1L)).thenReturn(Optional.of(lecturer));
        when(groupRepo.findAllById(updateRequest.getGroupIds())).thenReturn(List.of(exampleGroupEntity));
        when(lecturerRepo.save(any(LecturerEntity.class))).thenAnswer(invocation -> {
            LecturerEntity savedLecturer = invocation.getArgument(0);
            lecturer.setPhoneNumber(savedLecturer.getPhoneNumber());
            return lecturer;
        });
        LecturerDataResponse updateResponse = lecturerService.updateLecturer(updateRequest);
        assertNotNull(updateResponse);
        assertEquals(updateRequest.getEmail(), updateResponse.getEmail());
        assertEquals(updateRequest.getFirstName(), updateResponse.getFirstName());
        assertEquals(updateRequest.getLastName(), updateResponse.getLastName());
        assertEquals(updateRequest.getPhoneNumber(), updateResponse.getPhoneNumber());
        assertEquals(updateRequest.getCity(), updateResponse.getCity());
        assertEquals(updateRequest.getStatus(), updateResponse.getStatus());
        assertEquals(1, updateResponse.getGroupIds().size());
        assertTrue(updateResponse.getGroupIds().contains(exampleGroupEntity.getId()));
        verify(lecturerRepo, times(1)).findById(1L);
        verify(lecturerRepo, times(1)).save(any(LecturerEntity.class));
    }


    @Test
    void testUpdateLecturer_LecturerNotFound() {

        when(lecturerRepo.findById(updateRequest.getId())).thenReturn(Optional.empty());
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            lecturerService.updateLecturer(updateRequest);
        });
        assertEquals("Lecturer not found", exception.getMessage());
        verify(lecturerRepo, never()).save(any(LecturerEntity.class));
    }


    @Test
    void testDeleteLecturer_Success() {

        when(lecturerRepo.findById(1L)).thenReturn(Optional.of(lecturer));
        LecturerDataResponse response = lecturerService.deleteLecturer(1L);
        assertNotNull(response);
        assertEquals(response.getEmail(), lecturer.getEmail());
        assertEquals(response.getFirstName(), lecturer.getFirstName());
        assertEquals(response.getLastName(), lecturer.getLastName());
        assertEquals(response.getPhoneNumber(), lecturer.getPhoneNumber());
        assertEquals(response.getCity(), lecturer.getCity());
        assertEquals(response.getStatus(), lecturer.getStatus());
        assertEquals(1, response.getGroupIds().size());
        assertTrue(response.getGroupIds().contains(exampleGroupEntity.getId()));
        verify(lecturerRepo).deleteById(1L);
    }

    @Test
    void testDeleteLecturer_NotFound() {

        when(lecturerRepo.findById(anyLong())).thenReturn(Optional.empty());
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            lecturerService.deleteLecturer(1L);
        });
        assertEquals("Lecturer not found with id: 1", exception.getMessage());
        verify(lecturerRepo, never()).deleteById(anyLong());
    }
}
