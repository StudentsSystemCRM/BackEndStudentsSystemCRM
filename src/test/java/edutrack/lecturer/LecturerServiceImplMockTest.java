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
        exampleGroupEntity = new GroupEntity(
                1L,
                "Example Group",
                "example-whatsapp",
                "example-skype",
                "example-slack",
                GroupStatus.ACTIVE,
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 12, 31),
                false,
                new HashSet<>(),
                new ArrayList<>(),
                new HashMap<>(),
                new HashMap<>(),
                null,
                null,
                null,
                null
        );

        lecturer = new LecturerEntity(
                1L,
                "John",
                "Doe",
                "123456789",
                "john.doe@example.com",
                "Haifa",
                LecturerStatus.ACTIVE,
                Set.of(exampleGroupEntity),
                null,
                null
        );

        createRequest = new LecturerCreateRequest(
                "John",
                "Doe",
                "123456789",
                "john.doe@example.com",
                "Haifa",
                LecturerStatus.ACTIVE,
                Set.of(exampleGroupEntity.getId())
        );

        updateRequest = new LecturerUpdateRequest(
                1L,
                "John",
                "Doe",
                "987654321",
                "john.doe@example.com",
                "Haifa",
                LecturerStatus.ACTIVE,
                Set.of(exampleGroupEntity.getId())
        );
    }

    @Test
    void testGetLecturerById_Success() {
        when(lecturerRepo.findById(1L)).thenReturn(Optional.of(lecturer));

        LecturerDataResponse response = lecturerService.getLecturerById(1L);

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
        when(lecturerRepo.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> lecturerService.getLecturerById(1L));

        assertEquals("Lecturer not found with id: 1", exception.getMessage());
    }

    @Test
    void testFindLecturersByLastName_Success() {
        String lastName = "Doe";
        LecturerEntity secondLecturer = new LecturerEntity(
                2L,
                "Jane",
                "Doe",
                "987654321",
                "jane.doe@example.com",
                "Tel Aviv",
                LecturerStatus.ACTIVE,
                Set.of(exampleGroupEntity),
                null,
                null
        );
        when(lecturerRepo.findByLastName(lastName)).thenReturn(List.of(lecturer, secondLecturer));

        List<LecturerDataResponse> responses = lecturerService.findLecturersByLastName(lastName);

        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertLecturerDataResponse(responses.get(0), lecturer);
        assertLecturerDataResponse(responses.get(1), secondLecturer);
    }

    @Test
    void testFindLecturersByLastName_NotFound() {
        when(lecturerRepo.findByLastName("Smith")).thenReturn(Collections.emptyList());

        List<LecturerDataResponse> responses = lecturerService.findLecturersByLastName("Smith");

        assertNotNull(responses);
        assertTrue(responses.isEmpty());
    }

    @Test
    void testFindLecturersByStatus_Success() {
        LecturerStatus status = LecturerStatus.ACTIVE;
        LecturerEntity secondLecturer = new LecturerEntity(
                2L,
                "Jane",
                "Doe",
                "987654321",
                "jane.doe@example.com",
                "Tel Aviv",
                status,
                Set.of(exampleGroupEntity),
                null,
                null
        );
        when(lecturerRepo.findByStatus(status)).thenReturn(List.of(lecturer, secondLecturer));

        List<LecturerDataResponse> responses = lecturerService.findLecturersByStatus(status);

        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertLecturerDataResponse(responses.get(0), lecturer);
        assertLecturerDataResponse(responses.get(1), secondLecturer);
    }

    @Test
    void testFindLecturersByStatus_NotFound() {
        when(lecturerRepo.findByStatus(LecturerStatus.INACTIVE)).thenReturn(Collections.emptyList());

        List<LecturerDataResponse> responses = lecturerService.findLecturersByStatus(LecturerStatus.INACTIVE);

        assertNotNull(responses);
        assertTrue(responses.isEmpty());
    }

    @Test
    void testFindLecturersByCity_Success() {
        String city = "Haifa";
        LecturerEntity secondLecturer = new LecturerEntity(
                2L,
                "Jane",
                "Doe",
                "987654321",
                "jane.doe@example.com",
                city,
                LecturerStatus.ACTIVE,
                Set.of(exampleGroupEntity),
                null,
                null
        );
        when(lecturerRepo.findByCity(city)).thenReturn(List.of(lecturer, secondLecturer));

        List<LecturerDataResponse> responses = lecturerService.findLecturersByCity(city);

        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertLecturerDataResponse(responses.get(0), lecturer);
        assertLecturerDataResponse(responses.get(1), secondLecturer);
    }

    @Test
    void testFindLecturersByCity_NotFound() {
        when(lecturerRepo.findByCity("Unknown City")).thenReturn(Collections.emptyList());

        List<LecturerDataResponse> responses = lecturerService.findLecturersByCity("Unknown City");

        assertNotNull(responses);
        assertTrue(responses.isEmpty());
    }

    @Test
    void testGetAllLecturers_Success() {
        LecturerEntity secondLecturer = new LecturerEntity(
                2L,
                "Jane",
                "Doe",
                "987654321",
                "jane.doe@example.com",
                "Tel Aviv",
                LecturerStatus.ACTIVE,
                Set.of(exampleGroupEntity),
                null,
                null
        );
        when(lecturerRepo.findAll()).thenReturn(List.of(lecturer, secondLecturer));

        List<LecturerDataResponse> responses = lecturerService.getAllLecturers();

        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertLecturerDataResponse(responses.get(0), lecturer);
        assertLecturerDataResponse(responses.get(1), secondLecturer);
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

        verify(groupRepo).findAllById(createRequest.getGroupIds());
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
        when(lecturerRepo.save(any(LecturerEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        LecturerDataResponse response = lecturerService.updateLecturer(updateRequest);

        assertNotNull(response);
        assertEquals(updateRequest.getId(), response.getId());
        assertEquals(updateRequest.getEmail(), response.getEmail());
        assertEquals(updateRequest.getFirstName(), response.getFirstName());
        assertEquals(updateRequest.getLastName(), response.getLastName());
        assertEquals(updateRequest.getPhoneNumber(), response.getPhoneNumber());
        assertEquals(updateRequest.getCity(), response.getCity());
        assertEquals(updateRequest.getStatus(), response.getStatus());
        assertEquals(1, response.getGroupIds().size());
        assertTrue(response.getGroupIds().contains(exampleGroupEntity.getId()));

        verify(groupRepo).findAllById(updateRequest.getGroupIds());
    }

    @Test
    void testUpdateLecturer_NotFound() {
        updateRequest.setId(99L);

        when(lecturerRepo.findById(updateRequest.getId())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> lecturerService.updateLecturer(updateRequest));

        assertEquals("Lecturer not found with id: " + updateRequest.getId(), exception.getMessage());
    }

    @Test
    void testUpdateLecturer_EmailAlreadyInUse() {
        when(lecturerRepo.findById(updateRequest.getId())).thenReturn(Optional.of(lecturer));
        when(lecturerRepo.existsByEmail("johnTest.doe@example.com")).thenReturn(true);

        EmailAlreadyInUseException exception = assertThrows(EmailAlreadyInUseException.class, () -> {
            lecturerService.updateLecturer(new LecturerUpdateRequest(
                    1L,
                    "John",
                    "Doe",
                    "987654321",
                    "johnTest.doe@example.com",
                    "Haifa",
                    LecturerStatus.ACTIVE,
                    Set.of(exampleGroupEntity.getId())
            ));
        });

        assertEquals("Email already in use: johnTest.doe@example.com", exception.getMessage());
    }

    @Test
    void testUpdateLecturer_PartialUpdate() {
        LecturerUpdateRequest updateRequest = new LecturerUpdateRequest(
                1L,
                null,
                null,
                "987654321",
                null,
                null,
                null,
                Set.of()
        );

        when(lecturerRepo.findById(1L)).thenReturn(Optional.of(lecturer));
        when(lecturerRepo.save(any(LecturerEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        LecturerDataResponse response = lecturerService.updateLecturer(updateRequest);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Doe", response.getLastName());
        assertEquals("john.doe@example.com", response.getEmail());
        assertEquals("987654321", response.getPhoneNumber());
        assertEquals("John", response.getFirstName());
        assertEquals("Haifa", response.getCity());
        assertEquals(LecturerStatus.ACTIVE, response.getStatus());
    }

    @Test
    void testUpdateLecturer_InvalidGroupIds() {
        LecturerEntity existingLecturer = new LecturerEntity();
        existingLecturer.setId(1L);
        existingLecturer.setGroups(new HashSet<>());

        LecturerUpdateRequest updateRequest = new LecturerUpdateRequest();
        updateRequest.setId(1L);
        updateRequest.setGroupIds(Set.of(999L));

        when(lecturerRepo.findById(1L)).thenReturn(Optional.of(existingLecturer));
        when(groupRepo.findAllById(updateRequest.getGroupIds())).thenReturn(Collections.emptyList());

        assertThrows(ResourceNotFoundException.class, () -> lecturerService.updateLecturer(updateRequest));
    }

    @Test
    void testDeleteLecturer_Success() {
        when(lecturerRepo.findById(1L)).thenReturn(Optional.of(lecturer));
        lecturerService.deleteLecturer(1L);
        verify(lecturerRepo).deleteById(1L);
    }

    @Test
    void testDeleteLecturer_NotFound() {
        when(lecturerRepo.findById(1L)).thenReturn(Optional.empty());
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> lecturerService.deleteLecturer(1L));
        assertEquals("Lecturer not found with id: 1", exception.getMessage());
    }

    private void assertLecturerDataResponse(LecturerDataResponse response, LecturerEntity lecturer) {
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
}
