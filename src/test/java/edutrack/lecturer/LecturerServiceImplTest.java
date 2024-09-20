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
import edutrack.lecturer.util.EntityDtoLecturerMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class LecturerServiceImplTest {

    @Mock
    private LecturerRepository lecturerRepo;

    @Mock
    private GroupRepository groupRepo;

    @InjectMocks
    private LecturerServiceImpl lecturerService;

    private LecturerEntity lecturerEntity;
    private GroupEntity groupEntity;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        groupEntity = new GroupEntity("Group1",
                null,
                null,
                null,
                GroupStatus.ACTIVE,
                null,
                null,
                null,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                null,
                null);
        lecturerEntity = new LecturerEntity();
        lecturerEntity.setId(1L);
        lecturerEntity.setFirstName("John");
        lecturerEntity.setLastName("Doe");
        lecturerEntity.setStatus(LecturerStatus.ACTIVE);
        lecturerEntity.setGroups(Set.of(groupEntity));
    }

    @Test
    void createLecturer_ShouldReturnLecturerDataResponse() {
        LecturerCreateRequest request = new LecturerCreateRequest("John", "Doe", "123456789", "john.doe@example.com", "New York", LecturerStatus.ACTIVE, Set.of("Group1"));

        when(groupRepo.findByName("Group1")).thenReturn(groupEntity);
        when(lecturerRepo.save(any(LecturerEntity.class))).thenReturn(lecturerEntity); // Убедитесь, что здесь возвращается корректный объект

        LecturerDataResponse response = lecturerService.createLecturer(request);

        assertNotNull(response, "Response should not be null");
        assertEquals("John", response.getFirstName());
        assertEquals("Doe", response.getLastName());
        verify(lecturerRepo, times(1)).save(any(LecturerEntity.class));
    }


    @Test
    void getLecturerById_ShouldReturnLecturerDataResponse() {
        when(lecturerRepo.findById(1L)).thenReturn(Optional.of(lecturerEntity));

        LecturerDataResponse response = lecturerService.getLecturerById(1L);

        assertNotNull(response);
        assertEquals("John", response.getFirstName());
        assertEquals("Doe", response.getLastName());
    }

    @Test
    void getLecturerById_ShouldThrowResourceNotFoundException() {
        when(lecturerRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> lecturerService.getLecturerById(1L));
    }

    @Test
    void addGroupsToLecturer_ShouldAddGroups() {
        when(lecturerRepo.findById(1L)).thenReturn(Optional.of(lecturerEntity));
        when(groupRepo.findByName("Group1")).thenReturn(groupEntity);

        lecturerService.addGroupsToLecturer(1L, Set.of("Group1"));

        assertEquals(1, lecturerEntity.getGroups().size());
        verify(lecturerRepo, times(1)).save(lecturerEntity);
    }

    @Test
    void addGroupsToLecturer_ShouldThrowResourceNotFoundException() {
        when(lecturerRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> lecturerService.addGroupsToLecturer(1L, Set.of("Group1")));
    }

    @Test
    void updateLecturer_ShouldReturnUpdatedLecturerDataResponse() {
        LecturerUpdateRequest updateRequest = new LecturerUpdateRequest(1L, "John", "Doe", "123456789", "john.doe@example.com", "New York", LecturerStatus.ACTIVE, Set.of("Group1"));

        when(lecturerRepo.findById(1L)).thenReturn(Optional.of(lecturerEntity));
        when(groupRepo.findByName("Group1")).thenReturn(groupEntity);
        when(lecturerRepo.save(any(LecturerEntity.class))).thenReturn(lecturerEntity);

        LecturerDataResponse response = lecturerService.updateLecturer(updateRequest);

        assertNotNull(response);
        assertEquals("John", response.getFirstName());
        verify(lecturerRepo, times(1)).save(any(LecturerEntity.class));
    }

    @Test
    void updateLecturer_ShouldThrowResourceNotFoundException() {
        LecturerUpdateRequest updateRequest = new LecturerUpdateRequest(1L, "John", "Doe", "123456789", "john.doe@example.com", "New York", LecturerStatus.ACTIVE, Set.of("Group1"));

        when(lecturerRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> lecturerService.updateLecturer(updateRequest));
    }

    @Test
    void deleteLecturer_ShouldReturnDeletedLecturerDataResponse() {
        when(lecturerRepo.findById(1L)).thenReturn(Optional.of(lecturerEntity));

        LecturerDataResponse response = lecturerService.deleteLecturer(1L);

        assertNotNull(response);
        assertEquals("John", response.getFirstName());
        verify(lecturerRepo, times(1)).deleteById(1L);
    }

    @Test
    void deleteLecturer_ShouldThrowResourceNotFoundException() {
        when(lecturerRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> lecturerService.deleteLecturer(1L));
    }

    @Test
    void findLecturersByStatus_ShouldReturnListOfLecturerDataResponse() {
        when(lecturerRepo.findByStatus(LecturerStatus.ACTIVE)).thenReturn(Collections.singletonList(lecturerEntity));

        List<LecturerDataResponse> responses = lecturerService.findLecturersByStatus(LecturerStatus.ACTIVE);

        assertEquals(1, responses.size());
        assertEquals("John", responses.get(0).getFirstName());
    }

    @Test
    void findLecturersByCity_ShouldReturnListOfLecturerDataResponse() {
        when(lecturerRepo.findByCity("New York")).thenReturn(Collections.singletonList(lecturerEntity));

        List<LecturerDataResponse> responses = lecturerService.findLecturersByCity("New York");

        assertEquals(1, responses.size());
        assertEquals("John", responses.get(0).getFirstName());
    }

    @Test
    void getAllLecturers_ShouldReturnListOfLecturerDataResponse() {
        when(lecturerRepo.findAll()).thenReturn(Collections.singletonList(lecturerEntity));

        List<LecturerDataResponse> responses = lecturerService.getAllLecturers();

        assertEquals(1, responses.size());
        assertEquals("John", responses.get(0).getFirstName());
    }
    @Test
    void lecturerToLecturerDataResponse_ShouldMapLecturerEntityToResponse() {
        LecturerDataResponse response = EntityDtoLecturerMapper.INSTANCE.lecturerToLecturerDataResponse(lecturerEntity);
        assertNotNull(response);
        assertEquals("John", response.getFirstName());
        assertEquals("Doe", response.getLastName());
    }

}
