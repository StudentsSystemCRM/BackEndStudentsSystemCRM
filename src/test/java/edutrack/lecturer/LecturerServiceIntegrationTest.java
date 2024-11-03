package edutrack.lecturer;

import edutrack.exception.ResourceNotFoundException;
import edutrack.group.entity.GroupEntity;
import edutrack.group.repository.GroupRepository;
import edutrack.lecturer.constant.LecturerStatus;
import edutrack.lecturer.dto.request.LecturerCreateRequest;
import edutrack.lecturer.dto.request.LecturerUpdateRequest;
import edutrack.lecturer.dto.response.LecturerDataResponse;
import edutrack.lecturer.entity.LecturerEntity;
import edutrack.lecturer.repository.LecturerRepository;
import edutrack.lecturer.service.LecturerService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {
        "mailgun.api.key=disabled",
        "mailgun.domain=disabled",
        "mailgun.api.base-url=disabled",
        "mailgun.from-email=disabled",
        "mailgun.signature=disabled"
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Sql(scripts = {"classpath:test_data_lecturer.sql"})
@Transactional
public class LecturerServiceIntegrationTest {
    static final Long LECTURER_ID_DB_H2 = 2L;
    @Autowired
    private LecturerService lecturerService;

    @Autowired
    private LecturerRepository lecturerRepo;

    @Autowired
    private GroupRepository groupRepo;

    @Test
    void testCreateLecturerWithValidGroup() {

        GroupEntity existingGroup = groupRepo.findById(1L).orElse(null);
        assertNotNull(existingGroup, "Group with ID 1 should exist in the test database");

        Set<Long> groupIds = new HashSet<>();
        groupIds.add(existingGroup.getId());

        LecturerCreateRequest request = new LecturerCreateRequest(
                "John",
                "Doe",
                "123456789",
                "john.doe@example.com",
                "Haifa",
                LecturerStatus.ACTIVE,
                groupIds
        );

        LecturerDataResponse response = lecturerService.createLecturer(request);

        assertNotNull(response);
        assertEquals("John", response.getFirstName());
        assertEquals("Doe", response.getLastName());

        LecturerEntity savedLecturer = lecturerRepo.findById(response.getId()).orElse(null);
        assertNotNull(savedLecturer);
        assertEquals("John", savedLecturer.getFirstName());
        assertEquals("Doe", savedLecturer.getLastName());
        assertEquals("john.doe@example.com", savedLecturer.getEmail());

        Set<GroupEntity> groups = savedLecturer.getGroups();
        assertNotNull(groups);
        assertFalse(groups.isEmpty());
        assertTrue(groups.stream().anyMatch(group -> group.getId().equals(existingGroup.getId())));

        GroupEntity groupFromRepo = groupRepo.findById(existingGroup.getId()).orElse(null);
        assertNotNull(groupFromRepo, "Group should still exist in the repository after lecturer creation");
    }

    @Test
    void testGetLecturerById_Success() {
        Long lecturerId = LECTURER_ID_DB_H2;

        LecturerDataResponse response = lecturerService.getLecturerById(lecturerId);

        assertNotNull(response);
        assertEquals(lecturerId, response.getId());
        assertEquals("Jane", response.getFirstName());
        assertEquals("Smith", response.getLastName());
    }

    @Test
    void testUpdateLecturer_Success() {

        Long lecturerId = LECTURER_ID_DB_H2;

        LecturerEntity currentLecturer = lecturerRepo.findById(lecturerId).orElse(null);
        assertNotNull(currentLecturer);

        Set<Long> newGroupIds = new HashSet<>();
        newGroupIds.add(1L);

        LecturerUpdateRequest updateRequest = new LecturerUpdateRequest();
        updateRequest.setId(lecturerId);
        updateRequest.setEmail("updated.email@example.com");
        updateRequest.setGroupIds(newGroupIds);

        LecturerDataResponse response = lecturerService.updateLecturer(updateRequest);

        assertNotNull(response);
        assertEquals(lecturerId, response.getId());
        assertEquals("updated.email@example.com", response.getEmail());

        LecturerEntity updatedLecturer = lecturerRepo.findById(lecturerId).orElse(null);
        assertNotNull(updatedLecturer);
        assertEquals("updated.email@example.com", updatedLecturer.getEmail());

        Set<GroupEntity> groups = updatedLecturer.getGroups();
        assertNotNull(groups);
        assertFalse(groups.isEmpty());
        assertTrue(groups.stream().anyMatch(group -> group.getId().equals(1L)));
    }

    @Test
    void testDeleteLecturer_Success() {
        Long lecturerId = LECTURER_ID_DB_H2;

        LecturerEntity existingLecturer = lecturerRepo.findById(lecturerId).orElse(null);
        assertNotNull(existingLecturer);

        LecturerDataResponse response = lecturerService.deleteLecturer(lecturerId);

        assertNotNull(response);
        assertEquals(lecturerId, response.getId());
        assertEquals(existingLecturer.getFirstName(), response.getFirstName());
        assertEquals(existingLecturer.getLastName(), response.getLastName());

        assertThrows(ResourceNotFoundException.class, () -> {
            lecturerRepo.findById(lecturerId).orElseThrow(() -> new ResourceNotFoundException("Lecturer not found"));
        });
    }

    @Test
    void testDeleteLecturer_NotFound() {
        Long nonExistentLecturerId = 999L;

        assertThrows(ResourceNotFoundException.class, () -> {
            lecturerService.deleteLecturer(nonExistentLecturerId);
        });
    }
}



