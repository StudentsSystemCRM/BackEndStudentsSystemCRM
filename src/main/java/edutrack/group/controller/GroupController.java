package edutrack.group.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edutrack.group.constant.GroupStatus;
import edutrack.group.dto.request.GroupCreateRequest;
import edutrack.group.dto.request.GroupUpdateDataRequest;
import edutrack.group.dto.response.GroupDataResponse;
import edutrack.group.service.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GroupController {

	GroupService groupService;

	@PostMapping("/create")
	@Operation(summary = "Create a new group", description = "Creates a new group with the provided details.")
	public GroupDataResponse createGroup(@Valid @RequestBody GroupCreateRequest group) {
		return groupService.createGroup(group);
	}

	@GetMapping
	@Operation(summary = "Get all groups", description = "Returns a list of all groups.")
	public List<GroupDataResponse> getAllGroups() {
		return groupService.getAllGroups();
	}

	@GetMapping("/status")
	@Operation(summary = "Get groups by status", description = "Returns a list of groups filtered by their status.")
	public List<GroupDataResponse> getGroupsByStatus(@RequestParam GroupStatus status) {
		return groupService.getGroupsByStatus(status);
	}

	@GetMapping("/name/{name}")
	@Operation(summary = "Get group by name", description = "Returns the group that matches the given name.")
	public GroupDataResponse getGroupByName(@PathVariable String name) {
		return groupService.getGroupByName(name);
	}

	@GetMapping("/student/{id}")
	@Operation(summary = "Get groups of a student", description = "Returns a list of groups that a student belongs to.")
	public List<GroupDataResponse> getStudentGroups(@PathVariable @Min(0) Long id) {
		return groupService.getStudentGroups(id);
	}

	@PostMapping("/add-student")
	@Operation(summary = "Add a student to a group", description = "Adds a student to the specified group by student ID and group name.")
	public GroupDataResponse addStudentToGroup(@RequestParam @Min(0) Long id, @RequestParam String name) {
		return groupService.addStudentToGroup(id, name);
	}

	@PutMapping("/update")
	@Operation(summary = "Update group details", description = "Updates the details of an existing group. you cannot update name")
	public GroupDataResponse updateGroup(@RequestBody @Valid GroupUpdateDataRequest group) {
		return groupService.updateGroup(group);
	}

	@DeleteMapping("/remove-student")
	@Operation(summary = "Remove a student from a group", description = "Removes a student from the specified group by student ID and group name.")
	public GroupDataResponse deleteStudentFromGroup(@RequestParam @Min(0) Long id, @RequestParam String name) {
		return groupService.deleteStudentFromGroup(id, name);
	}

	@DeleteMapping("/delete/{name}")
	@Operation(summary = "Delete a group", description = "Deletes the group with the given name.")
	public GroupDataResponse deleteGroup(@PathVariable String name) {
		return groupService.deleteGroup(name);
	}

	@PutMapping("/{studentId}/{groupName}")
	@Operation(summary = "Change student's group", description = "Updates the group of a student to a new one.")
	public void changeStudentGroup(@PathVariable Long studentId, @PathVariable String groupName) {
		groupService.changeStudentGroup(studentId, groupName);
	}

}
