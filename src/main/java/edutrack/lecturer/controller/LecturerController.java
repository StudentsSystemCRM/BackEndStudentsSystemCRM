package edutrack.lecturer.controller;
import edutrack.lecturer.dto.request.LecturerCreateRequest;
import edutrack.lecturer.dto.response.LecturerDataResponse;
import edutrack.lecturer.service.LecturerServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lecturers")
@RequiredArgsConstructor
public class LecturerController {

    private final LecturerServiceImpl lecturerService;

    @PostMapping
    public ResponseEntity<LecturerDataResponse> createLecturer(@RequestBody LecturerCreateRequest request) {
        LecturerDataResponse response = lecturerService.createLecturer(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LecturerDataResponse> getLecturerById(@PathVariable Long id) {
        LecturerDataResponse response = lecturerService.getLecturerById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LecturerDataResponse> updateLecturer(@PathVariable Long id, @RequestBody LecturerCreateRequest request) {
        LecturerDataResponse response = lecturerService.updateLecturer(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLecturer(@PathVariable Long id) {
        lecturerService.deleteLecturer(id);
        return ResponseEntity.noContent().build();
    }

    // Другие методы...
}
