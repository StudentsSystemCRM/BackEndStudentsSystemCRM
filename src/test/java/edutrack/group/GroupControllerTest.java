package edutrack.group;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import edutrack.exception.StudentNotFoundException;
import edutrack.group.constant.GroupStatus;
import edutrack.group.constant.WeekDay;
import edutrack.group.controller.GroupController;
import edutrack.group.dto.request.GroupCreateRequest;
import edutrack.group.dto.request.GroupUpdateDataRequest;
import edutrack.group.dto.response.GroupDataResponse;
import edutrack.group.exception.GroupNotFoundException;
import edutrack.group.service.GroupService;
import edutrack.security.WebSecurityConfig;
import edutrack.security.jwt.JwtTokenProvider;
import edutrack.user.constant.ValidationAccountingMessage;
import edutrack.user.repository.AccountRepository;

@WebMvcTest(GroupController.class)
@Import({ JwtTokenProvider.class, WebSecurityConfig.class })
@AutoConfigureMockMvc(addFilters = false)
class GroupControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private GroupService groupService;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private AccountRepository userRepository;

	private GroupCreateRequest requestGroup = new GroupCreateRequest();
	private GroupDataResponse responseGroup = new GroupDataResponse();
	private List<GroupDataResponse> responseGroupList = new ArrayList<>();

	@Test
	void test() {
	}

	@BeforeEach
	void setUp() throws Exception {
		requestGroup = new GroupCreateRequest("java-24", "whatsApp", "skype", "slack", LocalDate.of(2024, 1, 1),
				LocalDate.of(2024, 6, 1), Arrays.asList(LocalDateTime.now(), LocalDateTime.now()),
				Arrays.asList(LocalDateTime.now(), LocalDateTime.now()));

		responseGroup = new GroupDataResponse(1L, "java-24", "whatsApp", "skype", "slack", GroupStatus.ACTIVE,
				LocalDate.of(2024, 1, 1), LocalDate.of(2024, 6, 1), Arrays.asList(LocalDateTime.now(), LocalDateTime.now()),
				Arrays.asList(LocalDateTime.now(), LocalDateTime.now()), false);
		responseGroupList = Arrays.asList(responseGroup);

	}

	@Test
	void shouldCreateGroup_whenValidRequest() throws Exception {
		when(groupService.createGroup(any(GroupCreateRequest.class))).thenReturn(responseGroup);

		mockMvc.perform(post("/api/groups/create").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestGroup))).andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("java-24")).andExpect(jsonPath("$.whatsApp").value("whatsApp"))
				.andExpect(jsonPath("$.status").value("ACTIVE")).andExpect(jsonPath("$.startDate").value("2024-01-01"))
				.andExpect(jsonPath("$.expFinishDate").value("2024-06-01"))
				.andExpect(jsonPath("$.lessons[0]").value("MONDAY"))
				.andExpect(jsonPath("$.lessons[1]").value("WEDNESDAY"))
				.andExpect(jsonPath("$.webinars[0]").value("TUESDAY"))
				.andExpect(jsonPath("$.webinars[1]").value("THURSDAY"));
	}

	@Test
	void shouldReturnBadRequest_whenInvalidRequest() throws Exception {
		GroupCreateRequest invalidRequest = new GroupCreateRequest("", "", "", "", null, null, null, null);

		mockMvc.perform(post("/api/groups/create").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(invalidRequest))).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").isArray())
				.andExpect(jsonPath("$.message", hasItem(ValidationAccountingMessage.INVALID_NAME)));
	}

	@Test
	void shouldGetAllGroups() throws Exception {
		GroupDataResponse group2 = new GroupDataResponse(1L, // id
				"java-20", // name
				"whatsApp", // whatsApp
				"skype", // skype
				"slack", // slack
				GroupStatus.INACTIVE, // status
				LocalDate.of(2024, 2, 1), // startDate
				LocalDate.of(2024, 7, 1), // expFinishDate
				Arrays.asList(LocalDateTime.now()), // lessons
				Arrays.asList(LocalDateTime.now()), // webinars
				false // DeactivateAfter30Days
		);

		List<GroupDataResponse> groups = Arrays.asList(responseGroup, group2);

		// Создаем Pageable для теста
		Pageable pageable = PageRequest.of(0, 10, Sort.by("name").ascending());

		// Мокаем вызов сервиса
		when(groupService.getAllGroups(pageable)).thenReturn(groups);

		mockMvc.perform(get("/api/groups").param("page", "0").param("size", "10").param("sortBy", "name")
				.param("ascending", "true").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$[0].name").value("java-24")).andExpect(jsonPath("$[1].name").value("java-20"));
	}

	@Test
	void shouldGetGroupsByStatus_whenValidStatus() throws Exception {
		when(groupService.getGroupsByStatus(GroupStatus.ACTIVE)).thenReturn(Collections.singletonList(responseGroup));

		mockMvc.perform(get("/api/groups/status").param("status", "ACTIVE").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$[0].name").value("java-24"))
				.andExpect(jsonPath("$[0].status").value("ACTIVE"));
	}

	@Test
	void shouldReturnBadRequest_whenInvalidStatus() throws Exception {
		mockMvc.perform(
				get("/api/groups/status").param("status", "INVALID_STATUS").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	@Test
	void shouldGetGroupByName_whenValidName() throws Exception {
		// Arrange
		when(groupService.getGroupsByName("java-24")).thenReturn(responseGroupList);

		// Act & Assert
		mockMvc.perform(get("/api/groups/name/{name}", "java-24").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.name").value("java-24"))
				.andExpect(jsonPath("$.status").value("ACTIVE"));
	}

//	@Test
	void groupget_NameIsEmpty() throws Exception {
		mockMvc.perform(get("/api/groups/name/{name}", "").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}

	@Test
	void shouldReturnNotFound_whenGroupDoesNotExist() throws Exception {
		when(groupService.getGroupsByName("non-existent-group"))
				.thenThrow(new GroupNotFoundException("Group not found"));

		mockMvc.perform(get("/api/groups/name/{name}", "non-existent-group").contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.message").value("Group not found"));
	}

//	@Test
//	void shouldGetStudentGroups_whenValidId() throws Exception {
//		when(groupService.getStudentGroups(1L)).thenReturn(Collections.singletonList(responseGroup));
//
//		mockMvc.perform(get("/api/groups/student/{id}", 1L).contentType(MediaType.APPLICATION_JSON))
//				.andExpect(status().isOk()).andExpect(jsonPath("$[0].name").value("java-24"));
//	}

	@Test
	void shouldReturnBadRequest_whenInvalidStudentId() throws Exception {
		mockMvc.perform(get("/api/groups/student/{id}", -1).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

//	@Test
//	void shouldAddStudentToGroup_whenValidRequest() throws Exception {
//		when(groupService.addStudentToGroup(1L, "java-24")).thenReturn(responseGroup);
//
//		mockMvc.perform(post("/api/groups/add-student").param("id", "1").param("name", "java-24")
//				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
//				.andExpect(jsonPath("$.name").value("java-24"));
//	}

	@Test
	void shouldReturnBadRequest_whenIdIsNegative() throws Exception {
		mockMvc.perform(post("/api/groups/add-student").param("id", "-1").param("name", "java-24")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
	}

//	@Test
//	void shouldReturnNotFound_whenStudentNotFound() throws Exception {
//		when(groupService.addStudentToGroup(1L, "java-24"))
//				.thenThrow(new StudentNotFoundException("Student with id 1 not found"));
//
//		mockMvc.perform(post("/api/groups/add-student").param("id", "1").param("name", "java-24")
//				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound())
//				.andExpect(jsonPath("$.message").value("Student with id 1 not found"));
//	}

//	@Test
//	void addStudentToGroup_whenGroupNotFound() throws Exception {
//		when(groupService.addStudentToGroup(1L, "non-existent-group"))
//				.thenThrow(new GroupNotFoundException("Group with name non-existent-group not found"));
//
//		mockMvc.perform(post("/api/groups/add-student").param("id", "1").param("name", "non-existent-group")
//				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound())
//				.andExpect(jsonPath("$.message").value("Group with name non-existent-group not found"));
//	}

	@Test
	void shouldUpdateGroup_whenValidRequest() throws Exception {
		GroupUpdateDataRequest updateRequest = new GroupUpdateDataRequest(1L, // id (нужно передать значение типа Long)
				"java-24", // name
				"newWhatsApp", // whatsApp
				"newSkype", // skype
				"newSlack", // slack
				GroupStatus.ACTIVE, // status
				LocalDate.of(2024, 2, 1), // startDate
				LocalDate.of(2024, 7, 1), // expFinishDate
				Arrays.asList(LocalDateTime.now()), // lessonsDays
				Arrays.asList(LocalDateTime.now()), // webinarsDays
				false // DeactivateAfter30Days
		);

		responseGroup.setWhatsApp("newWhatsApp");

		when(groupService.updateGroup(any(GroupUpdateDataRequest.class))).thenReturn(responseGroup);

		mockMvc.perform(put("/api/groups/update").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateRequest))).andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("java-24")).andExpect(jsonPath("$.whatsApp").value("newWhatsApp"));
	}

	@Test
	void shouldReturnBadRequest_whenNameIsEmpty() throws Exception {
		GroupUpdateDataRequest updateRequest = new GroupUpdateDataRequest(1L, "", "newWhatsApp", // whatsApp
				"newSkype", // skype
				"newSlack", // slack
				GroupStatus.ACTIVE, // status
				LocalDate.of(2024, 2, 1), // startDate
				LocalDate.of(2024, 7, 1), // expFinishDate
				Arrays.asList(LocalDateTime.now()), // lessonsDays
				Arrays.asList(LocalDateTime.now()), // webinarsDays
				false // DeactivateAfter30Days
		);

		mockMvc.perform(put("/api/groups/update").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateRequest))).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value(ValidationAccountingMessage.INVALID_NAME));
	}

	@Test
	void shouldReturnNotFound_whenGroupNotFound() throws Exception {
		// Arrange
		GroupUpdateDataRequest updateRequest = new GroupUpdateDataRequest(1L, "non-existent-group", "newWhatsApp",
				"newSkype", "newSlack", GroupStatus.ACTIVE, LocalDate.of(2024, 2, 1), LocalDate.of(2024, 7, 1),
				Arrays.asList(LocalDateTime.now()), Arrays.asList(LocalDateTime.now()), false);

		when(groupService.updateGroup(any(GroupUpdateDataRequest.class)))
				.thenThrow(new GroupNotFoundException("Group with name non-existent-group not found"));

		mockMvc.perform(put("/api/groups/update").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateRequest))).andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("Group with name non-existent-group not found"));
	}

//	@Test
//	void shouldRemoveStudentFromGroup_whenValidRequest() throws Exception {
//		when(groupService.deleteStudentFromGroup(1L, "java-24")).thenReturn(true);
//		mockMvc.perform(delete("/api/groups/remove-student").param("id", "1").param("name", "java-24")
//				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
//	}

	@Test
	void shouldReturnBadRequest_whenStudentIdIsNegative() throws Exception {
		mockMvc.perform(delete("/api/groups/remove-student").param("id", "-1").param("name", "java-24")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
	}

//	@Test
//	void shouldReturnNotFound_whenStudentNotFoundInGroup() throws Exception {
//		when(groupService.deleteStudentFromGroup(1L, "java-24"))
//				.thenThrow(new StudentNotFoundException("Student with id 1 not found"));
//
//		mockMvc.perform(delete("/api/groups/remove-student").param("id", "1").param("name", "java-24")
//				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound())
//				.andExpect(jsonPath("$.message").value("Student with id 1 not found"));
//	}

//	@Test
//	void shouldReturnNotFound_whenGroupNotFoundForRemovingStudent() throws Exception {
//		when(groupService.deleteStudentFromGroup(1L, "non-existent-group"))
//				.thenThrow(new GroupNotFoundException("Group with name non-existent-group not found"));
//
//		mockMvc.perform(delete("/api/groups/remove-student").param("id", "1").param("name", "non-existent-group")
//				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound())
//				.andExpect(jsonPath("$.message").value("Group with name non-existent-group not found"));
//	}

	@Test
	void shouldDeleteGroup_whenValidName() throws Exception {
		when(groupService.deleteGroup(1L)).thenReturn(true);

		mockMvc.perform(delete("/api/groups/delete/{name}", "java-24").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.name").value("java-24"));
	}

	@Test
	void shouldReturnNotFound_whenGroupNotFoundForDeletion() throws Exception {
		when(groupService.deleteGroup(2L))
				.thenThrow(new GroupNotFoundException("Group with name non-existent-group not found"));
		mockMvc.perform(
				delete("/api/groups/delete/{id}", "2").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("Group with id 2 not found"));
	}

}