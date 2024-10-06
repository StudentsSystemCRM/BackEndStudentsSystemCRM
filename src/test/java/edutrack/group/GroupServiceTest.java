package edutrack.group;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import edutrack.group.constant.GroupStatus;
import edutrack.group.constant.WeekDay;
import edutrack.group.dto.request.GroupCreateRequest;
import edutrack.group.dto.request.GroupUpdateDataRequest;
import edutrack.group.dto.response.GroupDataResponse;
import edutrack.group.entity.GroupEntity;
import edutrack.group.exception.GroupNotFoundException;
import edutrack.group.repository.GroupRepository;
import edutrack.group.service.GroupServiceImp;
import edutrack.student.repository.StudentRepository;
import edutrack.student.constant.LeadStatus;
import edutrack.student.entity.StudentEntity;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

public class GroupServiceTest {

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    GroupServiceImp groupService;
    GroupEntity group, existingGroup, updatedGroup;
    StudentEntity student;
    GroupCreateRequest request;
    List<GroupEntity> groups, groups2;
    GroupUpdateDataRequest updateDataRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        group = new GroupEntity(
                1L, "Group A", "12345-WhatsApp", 
                "SkypeUser", "SlackChannel", 
                GroupStatus.ACTIVE, 
                LocalDate.of(2023, 10, 1), LocalDate.of(2024, 1, 1),                
                LocalDate.of(2024, 6, 30),
                new ArrayList<>(),
                new ArrayList<>(),
                List.of(WeekDay.MONDAY, WeekDay.WEDNESDAY), List.of(WeekDay.FRIDAY),
                "",
                ""
        );
        groups = new ArrayList<>();
        groups.add(group);
        
        student = new StudentEntity(1L, "John", "Doe", "123456789", "john.doe@example.com", "City", "Course", "Source",
				LeadStatus.CONSULTATION, "testGroup", 16000, groups, null, null, null, "", "");
        
        request = new GroupCreateRequest(
                "Group A",
                "12345-WhatsApp",
                "SkypeUser",
                "SlackChannel",
                LocalDate.of(2023, 10, 1),
                LocalDate.of(2024, 1, 1),
                List.of(WeekDay.MONDAY, WeekDay.WEDNESDAY),
                List.of(WeekDay.FRIDAY)
        );

        existingGroup = new GroupEntity(
                1L, "Original Group", "Original-WhatsApp", 
                "Original-Skype", "Original-Slack", 
                GroupStatus.ACTIVE, 
                LocalDate.of(2023, 10, 1), LocalDate.of(2024, 1, 1), 
                LocalDate.of(2024, 6, 30),
                new ArrayList<>(),
                new ArrayList<>(),
                List.of(WeekDay.MONDAY, WeekDay.WEDNESDAY), List.of(WeekDay.FRIDAY),
                "",
                ""
        );

        updatedGroup = new GroupEntity(
                1L, "Updated Group", "Updated-WhatsApp", 
                "Updated-Skype", "Updated-Slack", 
                GroupStatus.INACTIVE, 
                LocalDate.of(2023, 11, 1), LocalDate.of(2024, 2, 1), 
                LocalDate.of(2024, 6, 30),
                new ArrayList<>(),
                new ArrayList<>(),
                List.of(WeekDay.MONDAY, WeekDay.WEDNESDAY), List.of(WeekDay.FRIDAY),
                "",
                ""
        );
        
        updateDataRequest = new GroupUpdateDataRequest(
                1L,
                "Updated Group",
                "Updated-WhatsApp",
                "Updated-Skype",
                "Updated-Slack",
                GroupStatus.INACTIVE,
                LocalDate.of(2023, 11, 1),
                LocalDate.of(2024, 2, 1),
                List.of(WeekDay.TUESDAY, WeekDay.THURSDAY),
                List.of(WeekDay.SATURDAY),
                true                               
        );
        
    }

    @Test
    void testCreateGroup_Success() {
        when(groupRepository.save(any(GroupEntity.class))).thenReturn(group);
        GroupDataResponse response = groupService.createGroup(request);
        assertNotNull(response);
        assertEquals("Group A", response.getName());
        assertEquals(GroupStatus.ACTIVE, response.getStatus());
        assertEquals("12345-WhatsApp", response.getWhatsApp());
        assertEquals("SkypeUser", response.getSkype());
        assertEquals("SlackChannel", response.getSlack());
        assertEquals(LocalDate.of(2023, 10, 1), response.getStartDate());
        assertEquals(LocalDate.of(2024, 1, 1), response.getExpFinishDate());
//        assertEquals(List.of(WeekDay.MONDAY, WeekDay.WEDNESDAY), groupRepository.getLessonsDays(1L));
//        assertEquals(List.of(WeekDay.FRIDAY), groupRepository.getWebinarsDays(1L));
        assertFalse(response.getDeactivateAfter30Days());
        verify(groupRepository, times(1)).save(any(GroupEntity.class));
    }
    
    @Test
    void testUpdateGroup_Success() {
        when(groupRepository.findById(1L)).thenReturn(Optional.of(existingGroup));
        when(groupRepository.save(any(GroupEntity.class))).thenReturn(updatedGroup);
        GroupDataResponse response = groupService.updateGroup(updateDataRequest);
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Updated Group", response.getName());
        assertEquals("Updated-WhatsApp", response.getWhatsApp());
        assertEquals("Updated-Skype", response.getSkype());
        assertEquals("Updated-Slack", response.getSlack());
        assertEquals(GroupStatus.INACTIVE, response.getStatus());
        assertEquals(LocalDate.of(2023, 11, 1), response.getStartDate());
        assertEquals(LocalDate.of(2024, 2, 1), response.getExpFinishDate());
//        assertEquals(List.of(WeekDay.TUESDAY, WeekDay.THURSDAY), response.getLessons());
//        assertEquals(List.of(WeekDay.SATURDAY), response.getWebinars());
        assertTrue(response.getDeactivateAfter30Days());
        verify(groupRepository, times(1)).findById(1L);
        verify(groupRepository, times(1)).save(any(GroupEntity.class));
    }
    
   @Test
    void testGetAllGroups() {
        Pageable pageable = PageRequest.of(0, 10);
        List<GroupEntity> groups = List.of( existingGroup, updatedGroup);
        Page<GroupEntity> groupPage = new PageImpl<>(groups);
        when(groupRepository.findAll(pageable)).thenReturn(groupPage);
        List<GroupDataResponse> response = groupService.getAllGroups(pageable);
        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals("Original Group", response.get(0).getName());
        assertEquals(GroupStatus.ACTIVE, response.get(0).getStatus());
        verify(groupRepository, times(2)).findAll(pageable);
    }

    @Test
    void testGetGroupsByStatus() {
        GroupStatus status = GroupStatus.ACTIVE;
        when(groupRepository.findByStatus(status)).thenReturn(groups);
        List<GroupDataResponse> response = groupService.getGroupsByStatus(status);
        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals("Group A", response.get(0).getName());
        assertEquals(GroupStatus.ACTIVE, response.get(0).getStatus());
        verify(groupRepository, times(1)).findByStatus(status);
    }

    @Test
    void testGetGroupByName() {
        String groupName = "Group A";
        when(groupRepository.findByName(groupName)).thenReturn(group);
        GroupDataResponse response = groupService.getGroupByName(groupName);
        assertNotNull(response);
        assertEquals("Group A", response.getName());
        assertEquals(GroupStatus.ACTIVE, response.getStatus());
        verify(groupRepository, times(1)).findByName(groupName);
    }

    @Test
    void testGetStudentGroups() {
        Long studentId = 1L;
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        List<GroupDataResponse> response = groupService.getStudentGroups(studentId);
        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals("Group A", response.get(0).getName());
//        verify(studentRepository, times(1)).findById(studentId);
    }

    @Test
    void testAddStudentToGroup() {
        Long studentId = 1L;
        String groupName = "Group 1";
        when(groupRepository.findByName(groupName)).thenReturn(group);
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        GroupDataResponse response = groupService.addStudentToGroup(studentId, groupName);
        assertNotNull(response);
        assertEquals("Group A", response.getName());
        verify(groupRepository, times(1)).findByName(groupName);
        verify(studentRepository, times(1)).findById(studentId);
        verify(groupRepository, times(1)).save(group);
    }

    @Test
    void testDeleteGroup_Success() {
        String groupName = "Group A";
        when(groupRepository.findByName(groupName)).thenReturn(group);
        GroupDataResponse response = groupService.deleteGroup(groupName);
        assertNotNull(response);
        assertEquals("Group A", response.getName());
        verify(groupRepository, times(1)).findByName(groupName);
//        verify(groupRepository, times(1)).delete(group);
    }
    
	@Test
	public void testDeleteGroup_NotFound() {
		String groupName = "Group 1";
		when(groupRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(GroupNotFoundException.class, () -> groupService.deleteGroup(groupName));

		verify(groupRepository, times(1)).findByName(groupName);
		verify(groupRepository, times(0)).delete(any(GroupEntity.class));
	}
	
//    @Test
    void testDeleteStudentFromGroup() {
        Long studentId = 1L;
        String groupName = "Group A";
      
        when(groupRepository.findByName(groupName)).thenReturn(group);
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));

        Boolean result = groupService.deleteStudentFromGroup(studentId, groupName);

        assertTrue(result);

        verify(groupRepository, times(1)).findByName(groupName);
        verify(studentRepository, times(1)).findById(studentId);
        verify(groupRepository, times(1)).save(group);
    }
}
