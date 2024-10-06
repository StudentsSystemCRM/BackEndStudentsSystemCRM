package edutrack.schedule.controller;

import edutrack.schedule.dto.request.AddGroupScheduleRequest;
import edutrack.schedule.dto.response.GroupScheduleResponse;
import edutrack.schedule.service.GroupScheduleService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/api/schedules/groups")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GroupScheduleController {

	GroupScheduleService groupSchedule;

	@GetMapping("/{name}")
	@Operation(summary = "Get a student's schedules by name", description = "Provide a name to lookup a specific group's schedules.")
	public GroupScheduleResponse getGroupReminders(@PathVariable String name) {
		return groupSchedule.getGroupReminders(name);
	}

	@PostMapping("/schedule")
	@Operation(summary = "Add a schedule to a group.", description = "Provide the necessary data to create a new schedule to a specific group.")
	public GroupScheduleResponse addGroupReminder(@RequestBody @Valid AddGroupScheduleRequest groupReminder) {
		return groupSchedule.addGroupReminder(groupReminder);
	}
}
