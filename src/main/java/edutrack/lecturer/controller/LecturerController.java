package edutrack.lecturer.controller;

import edutrack.exception.ResourceNotFoundException;
import edutrack.lecturer.constant.LecturerStatus;
import edutrack.lecturer.dto.request.LecturerCreateRequest;
import edutrack.lecturer.dto.request.LecturerUpdateRequest;
import edutrack.lecturer.dto.response.LecturerDataResponse;
import edutrack.lecturer.service.LecturerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import java.util.List;
import java.util.Set;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/lecturers")
@RequiredArgsConstructor
@Validated
public class LecturerController {

    private final LecturerService lecturerService;

    @Operation(summary = "Create a new lecturer",
            description = "Provide lecturer details to create a new lecturer.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Lecturer created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<LecturerDataResponse> createLecturer(@Valid @RequestBody LecturerCreateRequest request) {
        LecturerDataResponse response = lecturerService.createLecturer(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Add groups to an existing lecturer",
            description = "Provide an ID and group names to add groups to the lecturer.")
    @Parameter(name = "lecturerId", description = "ID of the lecturer to which groups will be added", required = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Groups added successfully"),
            @ApiResponse(responseCode = "404", description = "Lecturer not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/{lecturerId}/groups")
    public ResponseEntity<String> addGroupsToLecturer(@PathVariable Long lecturerId, @RequestBody Set<String> groupNames) {
        try {
            lecturerService.addGroupsToLecturer(lecturerId, groupNames);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    @Operation(summary = "Get lecturer by ID",
            description = "Provide an ID to lookup a specific lecturer.")
    @Parameter(name = "id", description = "ID of the lecturer to retrieve", required = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lecturer found"),
            @ApiResponse(responseCode = "404", description = "Lecturer not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<LecturerDataResponse> getLecturerById(@PathVariable Long id) {
        LecturerDataResponse response = lecturerService.getLecturerById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get lecturers by status",
            description = "Provide a status to retrieve lecturers with that status.")
    @Parameter(name = "status", description = "Status of lecturers to retrieve", required = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of lecturers with the specified status")
    })
    @GetMapping("/status")
    public ResponseEntity<List<LecturerDataResponse>> getLecturersByStatus(@RequestParam LecturerStatus status) {
        List<LecturerDataResponse> lecturers = lecturerService.findLecturersByStatus(status);
        return ResponseEntity.ok(lecturers);
    }

    @Operation(summary = "Get lecturers by city",
            description = "Provide a city to retrieve lecturers located in that city.")
    @Parameter(name = "city", description = "City of lecturers to retrieve", required = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of lecturers in the specified city")
    })
    @GetMapping("/city")
    public ResponseEntity<List<LecturerDataResponse>> getLecturersByCity(@RequestParam String city) {
        List<LecturerDataResponse> lecturers = lecturerService.findLecturersByCity(city);
        return ResponseEntity.ok(lecturers);
    }

    @Operation(summary = "Get all lecturers",
            description = "Retrieve a list of all lecturers.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of all lecturers")
    })
    @GetMapping
    public ResponseEntity<List<LecturerDataResponse>> getAllLecturers() {
        List<LecturerDataResponse> response = lecturerService.getAllLecturers();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update an existing lecturer",
            description = "Provide an ID and lecturer details to update the lecturer.")
    @Parameter(name = "id", description = "ID of the lecturer to update", required = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lecturer updated successfully"),
            @ApiResponse(responseCode = "404", description = "Lecturer not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PutMapping("/{id}")
    public ResponseEntity<LecturerDataResponse> updateLecturer(@PathVariable Long id, @Valid @RequestBody LecturerUpdateRequest request) {
        request.setId(id);
        LecturerDataResponse response = lecturerService.updateLecturer(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete a lecturer",
            description = "Provide an ID to delete a specific lecturer.")
    @Parameter(name = "id", description = "ID of the lecturer to delete", required = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lecturer deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Lecturer not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<LecturerDataResponse> deleteLecturer(@PathVariable Long id) {
        LecturerDataResponse deletedLecturer = lecturerService.deleteLecturer(id);
        return ResponseEntity.ok(deletedLecturer);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<String> handleValidationException(ValidationException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
}