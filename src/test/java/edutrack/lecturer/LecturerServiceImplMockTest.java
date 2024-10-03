package edutrack.lecturer;

import edutrack.exception.ResourceAlreadyExistsException;
import edutrack.group.repository.GroupRepository;
import edutrack.lecturer.constant.LecturerStatus;
import edutrack.lecturer.dto.request.LecturerCreateRequest;
import edutrack.lecturer.dto.request.LecturerUpdateRequest;
import edutrack.lecturer.dto.response.LecturerDataResponse;
import edutrack.lecturer.entity.LecturerEntity;
import edutrack.lecturer.repository.LecturerRepository;
import edutrack.lecturer.service.LecturerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class LecturerServiceImplMockTest {

    @Mock
    private LecturerRepository lecturerRepo;
    @Mock
    private GroupRepository groupRepository;
    @InjectMocks
    private LecturerServiceImpl lecturerService;

    private LecturerEntity existingLecturer;
    private LecturerCreateRequest createRequest;
    private LecturerUpdateRequest updateRequest;
    private LecturerDataResponse lecturerResponse;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        existingLecturer = new LecturerEntity(1L, "John", "Doe", "123456789", "john.doe@example.com", "Haifa", LecturerStatus.ACTIVE, new HashSet<>(), null, null);
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
    void createLecturer_ShouldThrowException_WhenEmailAlreadyExists() {
        // Arrange
        when(lecturerRepo.existsByEmail(createRequest.getEmail())).thenReturn(true);

        // Act & Assert
        assertThrows(ResourceAlreadyExistsException.class, () -> {
            lecturerService.createLecturer(createRequest);
        });
    }

    @Test
    void createLecturer_ShouldReturnLecturerDataResponse_WhenCreatedSuccessfully() {
        // Arrange
        when(lecturerRepo.existsByEmail(createRequest.getEmail())).thenReturn(false);
        when(lecturerRepo.save(any(LecturerEntity.class))).thenReturn(existingLecturer);

        // Act
        LecturerDataResponse response = lecturerService.createLecturer(createRequest);

        // Assert
        assertNotNull(response);
        assertEquals(existingLecturer.getId(), response.getId());
        assertEquals(existingLecturer.getFirstName(), response.getFirstName());
        assertEquals(existingLecturer.getLastName(), response.getLastName());
        assertEquals(existingLecturer.getEmail(), response.getEmail());
        assertEquals(existingLecturer.getCity(), response.getCity());
        assertEquals(existingLecturer.getStatus(), response.getStatus());
    }

    @Test
    void testCreateLecturer() {
        when(lecturerRepo.save(any(LecturerEntity.class))).thenReturn(existingLecturer);

        LecturerDataResponse response = lecturerService.createLecturer(createRequest);

        assertNotNull(response);
        assertEquals("John", response.getFirstName());
        assertEquals("Doe", response.getLastName());

        verify(lecturerRepo, times(1)).save(any(LecturerEntity.class));
    }

    @Test
    void testGetLecturerById_NotFound() {
        Long lecturerId = 1L;

        when(lecturerRepo.findById(lecturerId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> lecturerService.getLecturerById(lecturerId));

        verify(lecturerRepo, times(1)).findById(lecturerId);
    }

    @Test
    void testUpdateLecturer() {
        Long lecturerId = 1L;

        when(lecturerRepo.findById(lecturerId)).thenReturn(Optional.of(existingLecturer));
        when(lecturerRepo.save(any(LecturerEntity.class))).thenReturn(existingLecturer);

        LecturerDataResponse response = lecturerService.updateLecturer(updateRequest);

        assertNotNull(response);
        assertEquals("john.doe@newmail.com", response.getEmail());

        verify(lecturerRepo, times(1)).findById(lecturerId);
        verify(lecturerRepo, times(1)).save(any(LecturerEntity.class));
    }

    @Test
    void testDeleteLecturer() {
        Long lecturerId = 1L;

        when(lecturerRepo.findById(lecturerId)).thenReturn(Optional.of(existingLecturer));

        LecturerDataResponse response = lecturerService.deleteLecturer(lecturerId);

        assertNotNull(response);
        assertEquals(existingLecturer.getId(), response.getId());

        verify(lecturerRepo, times(1)).findById(lecturerId);
        verify(lecturerRepo, times(1)).delete(existingLecturer);
    }

    @Test
    void testFindLecturersByStatus() {
        LecturerEntity lecturer1 = new LecturerEntity(null, "Alice", "Smith", "123456789", "alice@example.com", "New York", LecturerStatus.ACTIVE, new HashSet<>(), null, null);
        LecturerEntity lecturer2 = new LecturerEntity(null, "Bob", "Johnson", "987654321", "bob@example.com", "Los Angeles", LecturerStatus.ACTIVE, new HashSet<>(), null, null);

        when(lecturerRepo.findByStatus(LecturerStatus.ACTIVE)).thenReturn(Arrays.asList(lecturer1, lecturer2));

        List<LecturerDataResponse> response = lecturerService.findLecturersByStatus(LecturerStatus.ACTIVE);

        assertEquals(2, response.size());
        assertEquals("Alice", response.get(0).getFirstName());

        verify(lecturerRepo, times(1)).findByStatus(LecturerStatus.ACTIVE);
    }

    @Test
    void testFindLecturersByCity() {
        LecturerEntity lecturer = new LecturerEntity(1L, "Charlie", "Brown", "555555555", "charlie@example.com", "Haifa", LecturerStatus.ACTIVE, new HashSet<>(), null, null);

        when(lecturerRepo.findByCity("Haifa")).thenReturn(Collections.singletonList(lecturer));

        List<LecturerDataResponse> response = lecturerService.findLecturersByCity("Haifa");

        assertEquals(1, response.size());
        assertEquals("Charlie", response.get(0).getFirstName());

        verify(lecturerRepo, times(1)).findByCity("Haifa");
    }

    @Test
    void testGetAllLecturers() {
        LecturerEntity lecturer1 = new LecturerEntity(1L, "Alice", "Smith", "123456789", "alice@example.com", "New York", LecturerStatus.ACTIVE, new HashSet<>(), null, null);
        LecturerEntity lecturer2 = new LecturerEntity(2L, "Bob", "Johnson", "987654321", "bob@example.com", "Los Angeles", LecturerStatus.ACTIVE, new HashSet<>(), null, null);

        when(lecturerRepo.findAll()).thenReturn(Arrays.asList(lecturer1, lecturer2));

        List<LecturerDataResponse> response = lecturerService.getAllLecturers();

        assertEquals(2, response.size());
        assertEquals("Alice", response.get(0).getFirstName());

        verify(lecturerRepo, times(1)).findAll();
    }
}
