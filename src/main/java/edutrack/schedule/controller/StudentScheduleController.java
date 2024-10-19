package edutrack.schedule.controller;

import edutrack.schedule.dto.request.ScheduleCreateRequest;
import edutrack.schedule.dto.response.ScheduleResponse;
import edutrack.schedule.service.StudentScheduleService;

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
@RequestMapping("/api/schedules/students")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StudentScheduleController {

	StudentScheduleService studentSchedule;

	@GetMapping("/{id}")
	@Operation(summary = "Get a student's schedules by ID", description = "Provide an ID to lookup a specific student's schedules.")
	public ScheduleResponse getStudentReminders(@PathVariable Long id) {
		return studentSchedule.getAllReminders(id);
	}

	@PostMapping("/schedule")
	@Operation(summary = "Add a schedule to a student.", description = "Provide the necessary data to create a new schedule to a specific student.")
	public ScheduleResponse addStudentComment(@RequestBody @Valid ScheduleCreateRequest studentReminder) {
		return studentSchedule.addReminder(studentReminder);
	}
}
