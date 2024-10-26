package edutrack.schedule.controller;

import edutrack.schedule.dto.request.ScheduleCreateRequest;
import edutrack.schedule.dto.request.ScheduleUpdateDataRequest;
import edutrack.schedule.dto.response.ScheduleResponse;
import edutrack.schedule.service.StudentScheduleService;

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
@RequestMapping("/api/schedules/students")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StudentScheduleController {

	StudentScheduleService studentSchedule;

	@GetMapping("/{id}")
	@Operation(summary = "Get a student schedule by id", description = "Provide a shedule id to lookup schedule.")
	public ScheduleResponse getSchedule(@PathVariable Long scheduleId) {
		return studentSchedule.getSchedule(scheduleId);
	}
	
	@GetMapping("/all/{id}")
	@Operation(summary = "Get all student's schedules by student id", description = "Provide a student id to lookup all specific student's schedules.")
	public ScheduleResponse getstudentSchedulers(@PathVariable Long id) {
		return studentSchedule.getAllSchedulers(id);
	}

	@PostMapping("/schedule")
	@Operation(summary = "Add a schedule to a student", description = "Provide the necessary data to create a new schedule to a specific student.")
	public ScheduleResponse addstudentSchedule(@RequestBody @Valid ScheduleCreateRequest scheduleCreateRequest) {
		return studentSchedule.addSchedule(scheduleCreateRequest);
	}
	
	@PutMapping("/update")
	@Operation(summary = "Update schedule details", description = "Updates the details of an existing schedule.")
	public ScheduleResponse updateSchedule(@RequestBody @Valid ScheduleUpdateDataRequest schedule) {
		return studentSchedule.updateSchedule(schedule);
	}
	
	@DeleteMapping("/delete/{id}")
	@Operation(summary = "Delete a schedule", description = "Deletes the schedule with the given id.")
	public Boolean deletestudent(@PathVariable Long scheduleId) {
		return studentSchedule.deleteSchedule(scheduleId);
	}

	@DeleteMapping("/delete_all/{id}")
	@Operation(summary = "Delete all student schedules", description = "Deletes all student schedules with the given student id.")
	public Boolean deleteAllSchedulers(@PathVariable Long id) {
		return studentSchedule.deleteAllSchedulers(id);
	}

}
