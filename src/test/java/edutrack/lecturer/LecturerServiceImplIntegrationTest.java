package edutrack.lecturer;

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
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Transactional
class LecturerServiceImplIntegrationTest {

    @Autowired
    private LecturerRepository lecturerRepo;

    @Autowired
    private GroupRepository groupRepo;

    @Autowired
    private LecturerServiceImpl lecturerService;

    @BeforeEach
    void setUp() {
        lecturerRepo.deleteAll();
        groupRepo.deleteAll();

        LocalDate creationDate = LocalDate.now();
        LocalDate finishDate = creationDate.plusMonths(18);
        LocalDate deactivateDate = finishDate.plusDays(30);

        GroupEntity group1 = new GroupEntity(
        		1L,
                "Group1",
                "whatsApp1",
                "skype1",
                "slack1",
                GroupStatus.ACTIVE,
                creationDate,
                finishDate,
                deactivateDate,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                null, // createdBy
                null  // lastModifiedBy
        );

        groupRepo.save(group1);
    }

    @Test
    void testCreateLecturer() {
        LecturerCreateRequest request = new LecturerCreateRequest("John", "Doe", "123456789", "john.doe@example.com", "New York", LecturerStatus.ACTIVE, new HashSet<>());
        LecturerEntity lecturer = new LecturerEntity(null, "John", "Doe", "123456789", "john.doe@example.com", "New York", LecturerStatus.ACTIVE, new HashSet<>(), null, null);

        LecturerDataResponse response = lecturerService.createLecturer(request);

        assertNotNull(response);
        assertEquals("John", response.getFirstName());
        assertEquals("Doe", response.getLastName());
    }

    @Test
    void testGetLecturerById_NotFound() {
        Long lecturerId = 1L;
        assertThrows(RuntimeException.class, () -> lecturerService.getLecturerById(lecturerId));
    }

    @Test
    void testUpdateLecturer() {

        LecturerEntity existingLecturer = new LecturerEntity(null, "John", "Doe", "123456789", "john.doe@example.com", "New York", LecturerStatus.ACTIVE, new HashSet<>(), null, null);
        lecturerRepo.save(existingLecturer);


        Long lecturerId = existingLecturer.getId();


        LecturerUpdateRequest updateRequest = new LecturerUpdateRequest(1L,"John", "Doe", "987654321", "john.doe@newmail.com", "Los Angeles", LecturerStatus.ACTIVE, new HashSet<>());


        LecturerDataResponse response = lecturerService.updateLecturer(updateRequest);


        assertNotNull(response);


        assertEquals("john.doe@newmail.com", response.getEmail());
        assertEquals("987654321", response.getPhoneNumber());
        assertEquals("Los Angeles", response.getCity());
        assertEquals(LecturerStatus.ACTIVE, response.getStatus());
    }


    @Test
    void testDeleteLecturer() {
        LecturerEntity lecturer = new LecturerEntity(1L, "John", "Doe", "123456789", "john.doe@example.com", "New York", LecturerStatus.ACTIVE, new HashSet<>(), null, null);
        lecturerRepo.save(lecturer);

        LecturerDataResponse response = lecturerService.deleteLecturer(lecturer.getId());

        assertNotNull(response);
        assertEquals(lecturer.getId(), response.getId());
    }

    @Test
    void testFindLecturersByStatus() {
        LecturerEntity lecturer1 = new LecturerEntity(null, "Alice", "Smith", "123456789", "alice@example.com", "New York", LecturerStatus.ACTIVE, new HashSet<>(), null, null);
        LecturerEntity lecturer2 = new LecturerEntity(null, "Bob", "Johnson", "987654321", "bob@example.com", "Los Angeles", LecturerStatus.ACTIVE, new HashSet<>(), null, null);

        lecturerRepo.saveAll(Arrays.asList(lecturer1, lecturer2));

        List<LecturerDataResponse> response = lecturerService.findLecturersByStatus(LecturerStatus.ACTIVE);

        assertEquals(2, response.size());
        assertEquals("Alice", response.get(0).getFirstName());
    }


    @Test
    void testFindLecturersByCity() {
        LecturerEntity lecturer = new LecturerEntity(1L, "Charlie", "Brown", "555555555", "charlie@example.com", "Chicago", LecturerStatus.ACTIVE, new HashSet<>(), null, null);
        lecturerRepo.save(lecturer);

        List<LecturerDataResponse> response = lecturerService.findLecturersByCity("Chicago");

        assertEquals(1, response.size());
        assertEquals("Charlie", response.get(0).getFirstName());
    }

    @Test
    void testGetAllLecturers() {
        LecturerEntity lecturer1 = new LecturerEntity(1L, "Alice", "Smith", "123456789", "alice@example.com", "New York", LecturerStatus.ACTIVE, new HashSet<>(), null, null);
        LecturerEntity lecturer2 = new LecturerEntity(2L, "Bob", "Johnson", "987654321", "bob@example.com", "Los Angeles", LecturerStatus.ACTIVE, new HashSet<>(), null, null);
        lecturerRepo.saveAll(Arrays.asList(lecturer1, lecturer2));

        List<LecturerDataResponse> response = lecturerService.getAllLecturers();

        assertEquals(2, response.size());
        assertEquals("Alice", response.get(0).getFirstName());
    }
}
