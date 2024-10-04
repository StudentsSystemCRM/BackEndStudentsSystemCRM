package edutrack.lecturer;

import edutrack.exception.ResourceAlreadyExistsException;
import edutrack.exception.ResourceNotFoundException;
import edutrack.group.repository.GroupRepository;
import edutrack.lecturer.constant.LecturerStatus;
import edutrack.lecturer.dto.request.LecturerCreateRequest;
import edutrack.lecturer.dto.request.LecturerUpdateRequest;
import edutrack.lecturer.dto.response.LecturerDataResponse;
import edutrack.lecturer.repository.LecturerRepository;
import edutrack.lecturer.service.LecturerServiceImpl;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = {"classpath:testdata.sql"})
public class LecturerServiceIntegrationTest {

    @Autowired
    private LecturerServiceImpl lecturerService;

    @Autowired
    private LecturerRepository lecturerRepo;

    @Autowired
    private GroupRepository groupRepository;

    @Test
    @Transactional
    public void testGetLecturerById_Success() {
        Long lecturerId = 1L; // Предполагается, что в testdata.sql есть лектор с id 1
        LecturerDataResponse response = lecturerService.getLecturerById(lecturerId);
        assertNotNull(response);
        assertEquals(lecturerId, response.getId());
    }

    @Test
    public void testGetLecturerById_NotFound() {
        Long lecturerId = 99L; // Не существующий id
        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            lecturerService.getLecturerById(lecturerId);
        });
        assertEquals("Lecturer not found with id: " + lecturerId, exception.getMessage());
    }

    @Test
    public void testCreateLecturer_Success() {
        LecturerCreateRequest request = new LecturerCreateRequest("Jane", "Doe", "123456789", "jane@example.com", "Haifa", LecturerStatus.ACTIVE, new HashSet<>(Set.of("Example Group")));
        LecturerDataResponse response = lecturerService.createLecturer(request);
        assertNotNull(response);
        assertEquals("Jane", response.getFirstName());
    }

    @Test
    public void testCreateLecturer_EmailAlreadyExists() {
        LecturerCreateRequest request = new LecturerCreateRequest("John", "Doe", "123456789", "john.doe@example.com", "Haifa", LecturerStatus.ACTIVE, new HashSet<>(Set.of("Example Group")));
        Exception exception = assertThrows(ResourceAlreadyExistsException.class, () -> {
            lecturerService.createLecturer(request);
        });
        assertEquals("Lecturer already exists with email: " + request.getEmail(), exception.getMessage());
    }

    @Test
    public void testUpdateLecturer_Success() {
        Long lecturerId = 1L; // Предполагается, что в testdata.sql есть лектор с id 1
        LecturerUpdateRequest updateRequest = new LecturerUpdateRequest(lecturerId, "John", "Doe", "987654321", "john.doe@example.com", "Haifa", LecturerStatus.ACTIVE, new HashSet<>(Set.of("nameExample")));
        LecturerDataResponse response = lecturerService.updateLecturer(updateRequest);
        assertNotNull(response);
        assertEquals(lecturerId, response.getId());
        assertEquals("987654321", response.getPhoneNumber());
    }

    @Test
    public void testUpdateLecturer_EmailAlreadyExists() {
        Long lecturerId = 1L; // Предполагается, что в testdata.sql есть лектор с id 1
        LecturerUpdateRequest updateRequest = new LecturerUpdateRequest(lecturerId, "John", "Doe", "123456789", "john.doe@example.com", "Haifa", LecturerStatus.ACTIVE, new HashSet<>(Set.of("nameExample")));
        Exception exception = assertThrows(ResourceAlreadyExistsException.class, () -> {
            lecturerService.updateLecturer(updateRequest);
        });
        assertEquals("Lecturer already exists with email: " + updateRequest.getEmail(), exception.getMessage());
    }

    @Test
    public void testDeleteLecturer_Success() {
        Long lecturerId = 1L; // Предполагается, что в testdata.sql есть лектор с id 1
        LecturerDataResponse response = lecturerService.deleteLecturer(lecturerId);
        assertNotNull(response);
        assertEquals(lecturerId, response.getId());
    }

    @Test
    public void testDeleteLecturer_NotFound() {
        Long lecturerId = 99L; // Не существующий id
        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            lecturerService.deleteLecturer(lecturerId);
        });
        assertEquals("Lecturer not found with id: " + lecturerId, exception.getMessage());
    }

    @Test
    public void testGetAllLecturers_Success() {
        List<LecturerDataResponse> responses = lecturerService.getAllLecturers();
        assertNotNull(responses);
        assertTrue(responses.size() > 0); // Предполагается, что в testdata.sql есть хотя бы один лектор
    }

    @Test
    public void testGetAllLecturers_Empty() {
        // создать отдельный SQL-скрипт для очистки

    }
}

