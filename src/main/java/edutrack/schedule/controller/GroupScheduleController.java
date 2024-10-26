package edutrack.schedule.controller;

import edutrack.schedule.dto.request.ScheduleCreateRequest;
import edutrack.schedule.dto.request.ScheduleUpdateDataRequest;
import edutrack.schedule.dto.response.ScheduleResponse;
import edutrack.schedule.service.GroupScheduleService;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

	@GetMapping("/{id}")
	@Operation(summary = "Get a group schedule by id", description = "Provide a shedule id to lookup schedule.")
	public ScheduleResponse getSchedule(@PathVariable Long scheduleId) {
		return groupSchedule.getSchedule(scheduleId);
	}
	
	@GetMapping("/all/{id}")
	@Operation(summary = "Get all group's schedules by group id", description = "Provide a group id to lookup all specific group's schedules.")
	public ScheduleResponse getGroupSchedulers(@PathVariable Long id) {
		return groupSchedule.getAllSchedulers(id);
	}

	@PostMapping("/schedule")
	@Operation(summary = "Add a schedule to a group", description = "Provide the necessary data to create a new schedule to a specific group.")
	public ScheduleResponse addGroupSchedule(@RequestBody @Valid ScheduleCreateRequest scheduleCreateRequest) {
		return groupSchedule.addSchedule(scheduleCreateRequest);
	}
	
	@PutMapping("/update")
	@Operation(summary = "Update schedule details", description = "Updates the details of an existing schedule.")
	public ScheduleResponse updateSchedule(@RequestBody @Valid ScheduleUpdateDataRequest schedule) {
		return groupSchedule.updateSchedule(schedule);
	}
	
	@DeleteMapping("/delete/{id}")
	@Operation(summary = "Delete a schedule", description = "Deletes the schedule with the given id.")
	public Boolean deleteGroup(@PathVariable Long scheduleId) {
		return groupSchedule.deleteSchedule(scheduleId);
	}

	@DeleteMapping("/delete_all/{id}")
	@Operation(summary = "Delete all group schedules", description = "Deletes all group schedules with the given group id.")
	public Boolean deleteAllSchedulers(@PathVariable Long id) {
		return groupSchedule.deleteAllSchedulers(id);
	}

	@MessageMapping("/messages")
	@Operation(summary = "Send messages with shedulers", description = "Send messages with shedulers")
    public String handle(String message) {
        return message;
    }
}
