package edutrack.lecturer;

import edutrack.exception.ResourceAlreadyExistsException;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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
                "nameExample",
                "example-whatsapp",
                "example-skype",
                "example-slack",
                GroupStatus.ACTIVE,
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 12, 31),
                LocalDate.of(2024, 6, 30),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                null,
                null
        );

        Set<GroupEntity> groups = new HashSet<>();
        groups.add(exampleGroupEntity);

        lecturer = new LecturerEntity(1L, "John", "Doe",
                "123456789", "john.doe@example.com", "Haifa",
                LecturerStatus.ACTIVE, groups, null, null);

        createRequest = new LecturerCreateRequest(
                "John", "Doe", "123456789", "john.doe@example.com",
                "Haifa", LecturerStatus.ACTIVE, new HashSet<>(Set.of("Example Group"))
        );
        updateRequest = new LecturerUpdateRequest(
                1L, "John", "Doe", "987654321",
                "john.doe@example.com", "Haifa", LecturerStatus.ACTIVE,
                new HashSet<>(Set.of("nameExample"))
        );
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
        assertEquals(lecturer.getEmail(), response.getEmail());
        assertEquals(lecturer.getStatus(), response.getStatus());

        verify(lecturerRepo, times(1)).findById(lecturerId);
    }
    @Test
    void testGetLecturerById_NotFound() {
        Long lecturerId = 1L;
        when(lecturerRepo.findById(lecturerId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            lecturerService.getLecturerById(lecturerId);
        });

        assertEquals("Lecturer not found with id: " + lecturerId, exception.getMessage());
        verify(lecturerRepo, times(1)).findById(lecturerId);
    }

    @Test
    void testFindLecturersByLastName_Success() {
        String lastName = "Doe";
        List<LecturerEntity> lecturers = List.of(lecturer);
        when(lecturerRepo.findByLastName(lastName)).thenReturn(lecturers);

        List<LecturerDataResponse> responses = lecturerService.findLecturersByLastName(lastName);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(lecturer.getId(), responses.get(0).getId());
        assertEquals(lecturer.getFirstName(), responses.get(0).getFirstName());

        verify(lecturerRepo, times(1)).findByLastName(lastName);
    }

    @Test
    void testFindLecturersByLastName_NotFound() {
        String lastName = "NonExistent";
        when(lecturerRepo.findByLastName(lastName)).thenReturn(Collections.emptyList());

        List<LecturerDataResponse> responses = lecturerService.findLecturersByLastName(lastName);

        assertNotNull(responses);
        assertTrue(responses.isEmpty());
        verify(lecturerRepo, times(1)).findByLastName(lastName);
    }

    @Test
    void testFindLecturersByStatus_Success() {
        LecturerStatus status = LecturerStatus.ACTIVE;
        List<LecturerEntity> lecturers = List.of(lecturer);
        when(lecturerRepo.findByStatus(status)).thenReturn(lecturers);

        List<LecturerDataResponse> responses = lecturerService.findLecturersByStatus(status);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(lecturer.getId(), responses.get(0).getId());
        assertEquals(lecturer.getFirstName(), responses.get(0).getFirstName());

        verify(lecturerRepo, times(1)).findByStatus(status);
    }

    @Test
    void testFindLecturersByStatus_NotFound() {
        LecturerStatus status = LecturerStatus.INACTIVE;
        when(lecturerRepo.findByStatus(status)).thenReturn(Collections.emptyList());

        List<LecturerDataResponse> responses = lecturerService.findLecturersByStatus(status);

        assertNotNull(responses);
        assertTrue(responses.isEmpty());
        verify(lecturerRepo, times(1)).findByStatus(status);
    }
    @Test
    void testFindLecturersByCity_Success() {
        String city = "Haifa";
        List<LecturerEntity> lecturers = List.of(lecturer);
        when(lecturerRepo.findByCity(city)).thenReturn(lecturers);

        List<LecturerDataResponse> responses = lecturerService.findLecturersByCity(city);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(lecturer.getId(), responses.get(0).getId());
        assertEquals(lecturer.getFirstName(), responses.get(0).getFirstName());

        verify(lecturerRepo, times(1)).findByCity(city);
    }

    @Test
    void testFindLecturersByCity_NotFound() {
        String city = "NonExistentCity";
        when(lecturerRepo.findByCity(city)).thenReturn(Collections.emptyList());

        List<LecturerDataResponse> responses = lecturerService.findLecturersByCity(city);

        assertNotNull(responses);
        assertTrue(responses.isEmpty());
        verify(lecturerRepo, times(1)).findByCity(city);
    }
    @Test
    void testGetAllLecturers_Success() {
        LecturerEntity anotherLecturer = new LecturerEntity(2L, "Jane", "Smith",
                "987654321", "jane.smith@example.com", "Tel Aviv",
                LecturerStatus.ACTIVE, new HashSet<>(), null, null);
        List<LecturerEntity> lecturers = List.of(lecturer, anotherLecturer);
        when(lecturerRepo.findAll()).thenReturn(lecturers);

        List<LecturerDataResponse> responses = lecturerService.getAllLecturers();

        assertNotNull(responses);
        assertEquals(2, responses.size());

        assertEquals(lecturer.getId(), responses.get(0).getId());
        assertEquals(lecturer.getFirstName(), responses.get(0).getFirstName());

        assertEquals(anotherLecturer.getId(), responses.get(1).getId());
        assertEquals(anotherLecturer.getFirstName(), responses.get(1).getFirstName());

        verify(lecturerRepo, times(1)).findAll();
    }
    @Test
    void testGetAllLecturers_Empty() {
        when(lecturerRepo.findAll()).thenReturn(Collections.emptyList());

        List<LecturerDataResponse> responses = lecturerService.getAllLecturers();

        assertNotNull(responses);
        assertTrue(responses.isEmpty());
        verify(lecturerRepo, times(1)).findAll();
    }
    @Test
    void testCreateLecturer_Success() {
        when(lecturerRepo.existsByEmail(createRequest.getEmail())).thenReturn(false);
        when(groupRepo.findByName("Example Group")).thenReturn(exampleGroupEntity);
        when(lecturerRepo.save(any(LecturerEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        LecturerDataResponse response = lecturerService.createLecturer(createRequest);

        assertNotNull(response);
        assertEquals(createRequest.getFirstName(), response.getFirstName());
        assertEquals(createRequest.getLastName(), response.getLastName());
        assertEquals(createRequest.getEmail(), response.getEmail());

        verify(lecturerRepo, times(1)).existsByEmail(createRequest.getEmail());
        verify(groupRepo, times(1)).findByName("Example Group");
        verify(lecturerRepo, times(1)).save(any(LecturerEntity.class));
    }
    @Test
    void testCreateLecturer_EmailAlreadyExists() {
        when(lecturerRepo.existsByEmail(createRequest.getEmail())).thenReturn(true);

        Exception exception = assertThrows(ResourceAlreadyExistsException.class, () -> {
            lecturerService.createLecturer(createRequest);
        });

        assertEquals("Lecturer already exists with email: " + createRequest.getEmail(), exception.getMessage());
        verify(lecturerRepo, times(1)).existsByEmail(createRequest.getEmail());
        verify(lecturerRepo, never()).save(any(LecturerEntity.class));
        verify(groupRepo, never()).findByName(anyString());
    }

    @Test
    void testUpdateLecturer_Success() {

        Long lecturerId = 1L;
        when(lecturerRepo.findById(lecturerId)).thenReturn(Optional.of(lecturer));
        when(lecturerRepo.existsByEmail(updateRequest.getEmail())).thenReturn(false);
        when(groupRepo.findByName("nameExample")).thenReturn(exampleGroupEntity);
        when(lecturerRepo.save(any(LecturerEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        LecturerDataResponse response = lecturerService.updateLecturer(updateRequest);

        assertNotNull(response);
        assertEquals(updateRequest.getId(), response.getId());
        assertEquals(updateRequest.getFirstName(), response.getFirstName());
        assertEquals(updateRequest.getLastName(), response.getLastName());
        assertEquals(updateRequest.getEmail(), response.getEmail());
        assertEquals(updateRequest.getPhoneNumber(), response.getPhoneNumber());
        assertEquals(updateRequest.getCity(), response.getCity());
        assertEquals(updateRequest.getStatus(), response.getStatus());

        verify(lecturerRepo, times(1)).findById(lecturerId);
        verify(lecturerRepo, times(1)).existsByEmail(updateRequest.getEmail());
        verify(lecturerRepo, times(1)).save(any(LecturerEntity.class));
    }
    @Test
    void testUpdateLecturer_EmailAlreadyExists() {
        Long lecturerId = 1L;
        when(lecturerRepo.findById(lecturerId)).thenReturn(Optional.of(lecturer));
        when(lecturerRepo.existsByEmail(updateRequest.getEmail())).thenReturn(true);

        Exception exception = assertThrows(ResourceAlreadyExistsException.class, () -> {
            lecturerService.updateLecturer(updateRequest);
        });

        assertEquals("Lecturer already exists with email: " + updateRequest.getEmail(), exception.getMessage());
        verify(lecturerRepo, times(1)).findById(lecturerId);
        verify(lecturerRepo, times(1)).existsByEmail(updateRequest.getEmail());
        verify(lecturerRepo, never()).save(any(LecturerEntity.class));
    }

    @Test
    void testDeleteLecturer_Success() {
        Long lecturerId = lecturer.getId();
        when(lecturerRepo.findById(lecturerId)).thenReturn(Optional.of(lecturer));

        LecturerDataResponse response = lecturerService.deleteLecturer(lecturerId);

        assertNotNull(response);
        assertEquals(lecturer.getId(), response.getId());
        assertEquals(lecturer.getFirstName(), response.getFirstName());
        assertEquals(lecturer.getLastName(), response.getLastName());
        assertEquals(lecturer.getPhoneNumber(), response.getPhoneNumber());
        assertEquals(lecturer.getEmail(), response.getEmail());
        assertEquals(lecturer.getCity(), response.getCity());
        assertEquals(lecturer.getStatus(), response.getStatus());
        assertEquals(lecturer.getGroups().stream().map(GroupEntity::getName).collect(Collectors.toSet()), response.getGroupNames());

        verify(lecturerRepo, times(1)).findById(lecturerId);
        verify(lecturerRepo, times(1)).deleteById(lecturerId);
    }

    @Test
    void testDeleteLecturer_NotFound() {
        Long lecturerId = 99L;
        when(lecturerRepo.findById(lecturerId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            lecturerService.deleteLecturer(lecturerId);
        });

        assertEquals("Lecturer not found with id: " + lecturerId, exception.getMessage());
        verify(lecturerRepo, times(1)).findById(lecturerId);
        verify(lecturerRepo, times(0)).deleteById(lecturerId);
    }



}
