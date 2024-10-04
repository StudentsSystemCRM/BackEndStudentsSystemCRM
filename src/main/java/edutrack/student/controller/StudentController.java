package edutrack.student.controller;

import java.util.List;

import edutrack.student.dto.request.StudentCreateRequest;
import edutrack.student.dto.request.StudentUpdateDataRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import edutrack.student.service.StudentService;
import edutrack.student.dto.response.StudentDataResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/students")
public class StudentController {
    private final StudentService service;
    public StudentController(StudentService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Get all students", description = "Retrieve a list of all students.")
    public List<StudentDataResponse> getAllStudents() {
        return service.getAllStudents();
    }

    @PostMapping("/create_student")
    @Operation(summary = "Create new student", description = "Provide necessary data to create a new student.")
    public StudentDataResponse createStudent(@RequestBody @Valid StudentCreateRequest student) {
        return service.createStudent(student);
    }

    @GetMapping("/name")
    @Operation(summary = "Get a student by name", description = "Provide necessary a name of student.")
    public List<StudentDataResponse> getStudentsByName(@RequestParam String name) {
        return service.getStudentsByName(name);
    }

    @GetMapping("/surname")
    @Operation(summary = "Get a student by surname", description = "Provide necessary a surname of student.")
    public List<StudentDataResponse> getStudentsBySurname(@RequestParam String surname) {
        return service.getStudentsBySurname(surname);
    }

    @GetMapping("/name_and_surname")
    @Operation(summary = "Get a student by name and surname", description = "Provide necessary a name and surname of student.")
    public List<StudentDataResponse> getStudentsByNameAndSurname(@RequestParam String name, @RequestParam String surname) {
        return service.getStudentsByNameAndSurname(name, surname);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a student by ID", description = "Provide an ID to lookup a specific student.")
    public StudentDataResponse getStudentById(@PathVariable Long id) {
        return service.getStudentById(id);
    }

    @PutMapping("/update_student_information")
    @Operation(summary = "Update student information.", description = "Provide all data specific student and updated a needed fields.")
    public StudentDataResponse updateStudent(@RequestBody @Valid StudentUpdateDataRequest student) {
        return service.updateStudent(student);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a student by ID", description = "Provide an ID to delete a specific student.")
    public StudentDataResponse deleteStudent(@PathVariable Long id) {
        return service.deleteStudent(id);
    }
}
