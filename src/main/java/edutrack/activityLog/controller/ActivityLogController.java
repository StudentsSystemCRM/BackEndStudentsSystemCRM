package edutrack.activityLog.controller;

import edutrack.activityLog.dto.response.ActivityLogResponse;
import edutrack.activityLog.service.ActivityLogService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edutrack.activityLog.dto.request.AddActivityLogRequest;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/api/log_activites")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ActivityLogController {
	
	ActivityLogService activityLog;

	
    @GetMapping("/{id}/activity")
    @Operation(summary = "Get a student's activities by ID", description = "Provide an ID to lookup a specific student's activities.")
    public ActivityLogResponse getStudentActivityLog(@PathVariable Long id) {
        return activityLog.getStudentActivityLog(id);
    }
    
    @PostMapping("/comment")
    @Operation(summary = "Add a comment to a student.", description = "Provide the necessary data to create a new comment to a specific student.")
    public ActivityLogResponse addStudentComment(@RequestBody @Valid AddActivityLogRequest studentComment) {
        return activityLog.addActivityLog(studentComment);
    }
}
