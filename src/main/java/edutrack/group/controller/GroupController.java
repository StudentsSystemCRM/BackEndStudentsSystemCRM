package edutrack.group.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

	@PutMapping("/update")
	@Operation(summary = "Update group details", description = "Updates the details of an existing group.")
	public GroupDataResponse updateGroup(@RequestBody @Valid GroupUpdateDataRequest group) {
		return groupService.updateGroup(group);
	}
	
	@GetMapping
	@Operation(summary = "Get all groups", description = "Returns a list of all groups with params.")
	public List<GroupDataResponse> getAllGroups(@RequestParam(required = false, defaultValue = "0") int page,
			@RequestParam(required = false, defaultValue = "10") int size,
			@RequestParam(required = false, defaultValue = "name") String sortBy,
			@RequestParam(required = false, defaultValue = "true") boolean ascending) {
		Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
		Pageable pageable = PageRequest.of(page, size, sort);
		return groupService.getAllGroups(pageable);
	}

	@GetMapping("/status")
	@Operation(summary = "Get groups by status", description = "Returns a list of groups filtered by their status.")
	public List<GroupDataResponse> getGroupsByStatus(@RequestParam GroupStatus status) {
		return groupService.getGroupsByStatus(status);
	}

	@GetMapping("/name/{name}")
	@Operation(summary = "Get group by name", description = "Returns the group that matches the given name.")
	public List<GroupDataResponse> getGroupByName(@PathVariable String name) {
		return groupService.getGroupsByName(name);
	}

	@GetMapping("/groups/{ids}")
	@Operation(summary = "Get groups of a student by groups ids", description = "Returns a list of groups that matches the given list of groups ids.")
	public List<GroupDataResponse> getStudentGroups(@PathVariable List<Long> ids) {
		return groupService.getGroupsByGroupsIds(ids);
	}

	@GetMapping("/group/{id}")
	@Operation(summary = "Get students ids by group id", description = "Returns a list of students ids that matches the given group id.")
	public List<Long> getStudentsIdsByGroup(@PathVariable Long id) {
		return groupService.getStudentsIdsByGroup(id);
	}
	
	@DeleteMapping("/delete/{id}")
	@Operation(summary = "Delete a group", description = "Deletes the group with the given id.")
	public Boolean deleteGroup(@PathVariable Long id) {
		return groupService.deleteGroup(id);
	}

}