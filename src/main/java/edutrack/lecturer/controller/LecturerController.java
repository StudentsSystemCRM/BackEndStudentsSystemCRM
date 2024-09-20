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

@RestController
@RequestMapping("/lecturers")
@RequiredArgsConstructor
@Validated
public class LecturerController {

    private final LecturerService lecturerService;

    @PostMapping
    public ResponseEntity<LecturerDataResponse> createLecturer(@Valid @RequestBody LecturerCreateRequest request) {
        LecturerDataResponse response = lecturerService.createLecturer(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

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

    @GetMapping("/{id}")
    public ResponseEntity<LecturerDataResponse> getLecturerById(@PathVariable Long id) {
        LecturerDataResponse response = lecturerService.getLecturerById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status")
    public ResponseEntity<List<LecturerDataResponse>> getLecturersByStatus(@RequestParam LecturerStatus status) {
        List<LecturerDataResponse> lecturers = lecturerService.findLecturersByStatus(status);
        return ResponseEntity.ok(lecturers);
    }

    @GetMapping("/city")
    public ResponseEntity<List<LecturerDataResponse>> getLecturersByCity(@RequestParam String city) {
        List<LecturerDataResponse> lecturers = lecturerService.findLecturersByCity(city);
        return ResponseEntity.ok(lecturers);
    }

    @GetMapping
    public ResponseEntity<List<LecturerDataResponse>> getAllLecturers() {
        List<LecturerDataResponse> response = lecturerService.getAllLecturers();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LecturerDataResponse> updateLecturer(@PathVariable Long id, @Valid @RequestBody LecturerUpdateRequest request) {
        request.setId(id);
        LecturerDataResponse response = lecturerService.updateLecturer(request);
        return ResponseEntity.ok(response);
    }

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
