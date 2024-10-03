package edutrack.lecturer.controller;

import edutrack.lecturer.constant.LecturerStatus;
import edutrack.lecturer.dto.request.LecturerCreateRequest;
import edutrack.lecturer.dto.request.LecturerUpdateRequest;
import edutrack.lecturer.dto.response.LecturerDataResponse;
import edutrack.lecturer.service.LecturerService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@RestController
@RequestMapping("/api/lecturers")
@RequiredArgsConstructor
@Validated
public class LecturerController {

    private final LecturerService lecturerService;

    @Operation(summary = "Create a new lecturer",
            description = "Provide lecturer details to create a new lecturer.")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public LecturerDataResponse createLecturer(@Valid @RequestBody LecturerCreateRequest request) {
        return lecturerService.createLecturer(request);
    }

    @Operation(summary = "Get lecturer by ID",
            description = "Provide an ID to lookup a specific lecturer.")
    @Parameter(name = "id", description = "ID of the lecturer to retrieve", required = true)
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public LecturerDataResponse getLecturerById(@PathVariable Long id) {
        return lecturerService.getLecturerById(id);
    }

    @Operation(summary = "Get lecturers by last name",
            description = "Specify the last name to retrieve lecturers with that last name.")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/last-name")
    public List<LecturerDataResponse> getLecturersByLastName(@RequestParam @NotBlank String lastName) {
        return lecturerService.findLecturersByLastName(lastName);
    }

    @Operation(summary = "Get lecturers by status",
            description = "Provide a status to retrieve lecturers with that status.")
    @Parameter(name = "status", description = "Status of lecturers to retrieve", required = true)
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/status")
    public List<LecturerDataResponse> getLecturersByStatus(@RequestParam @NotNull LecturerStatus status) {
        return lecturerService.findLecturersByStatus(status);
    }

    @Operation(summary = "Get lecturers by city",
            description = "Provide a city to retrieve lecturers located in that city.")
    @Parameter(name = "city", description = "City of lecturers to retrieve", required = true)
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/city")
    public List<LecturerDataResponse> getLecturersByCity(@RequestParam @NotBlank  String city) {
        return lecturerService.findLecturersByCity(city);
    }

    @Operation(summary = "Get all lecturers",
            description = "Retrieve a list of all lecturers.")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<LecturerDataResponse> getAllLecturers() {
        return lecturerService.getAllLecturers();
    }

    @Operation(summary = "Update an existing lecturer",
            description = "Provide the ID and lecturer data for updating.")
    @Parameter(name = "id", description = "ID of the lecturer to be updated", required = true)
    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public LecturerDataResponse updateLecturer(@Valid @RequestBody LecturerUpdateRequest request) {
        return lecturerService.updateLecturer(request);
    }

    @Operation(summary = "Delete a lecturer",
            description = "Provide an ID to delete a specific lecturer.")
    @Parameter(name = "id", description = "ID of the lecturer to delete", required = true)
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{id}")
    public LecturerDataResponse deleteLecturer(@PathVariable Long id) {
        return lecturerService.deleteLecturer(id);
    }
}
