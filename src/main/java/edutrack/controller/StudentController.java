package edutrack.controller;

import edutrack.dto.request.students.AddStudentCommentRequest;
import edutrack.dto.request.students.AddStudentPaymentRequest;
import edutrack.dto.request.students.StudentCreateRequest;
import edutrack.dto.request.students.StudentUpdateDataRequest;
import edutrack.dto.response.students.StudentActivityLogResponse;
import edutrack.dto.response.students.StudentDataResponse;
import edutrack.dto.response.students.StudentPaymentInfoResponse;
import edutrack.service.IStudent;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController{
    private final IStudent service;
    @Autowired
    public StudentController(IStudent service) {
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
    public StudentDataResponse getStudentById(@PathVariable Integer id) {
        return service.getStudentById(id);
    }

    @GetMapping("/{id}/activity")
    @Operation(summary = "Get a student's activities by ID", description = "Provide an ID to lookup a specific student's activities.")
    public StudentActivityLogResponse getStudentActivityLog(@PathVariable Integer id) {
        return service.getStudentActivityLog(id);
    }

    @GetMapping("/{id}/payments")
    @Operation(summary = "Get a student's payments by ID", description = "Provide an ID to lookup a specific student's payments.")
    public StudentPaymentInfoResponse getStudentPaymentInfo(@PathVariable Integer id) {
        return service.getStudentPaymentInfo(id);
    }

    @PutMapping("/update_student_information")
    @Operation(summary = "Update student information.", description = "Provide all data specific student and updated a needed fields.")
    public StudentDataResponse updateStudent(@RequestBody @Valid StudentUpdateDataRequest student) {
        return service.updateStudent(student);
    }

    @PostMapping("/comment")
    @Operation(summary = "Add a comment to a student.", description = "Provide the necessary data to create a new comment to a specific student.")
    public StudentActivityLogResponse addStudentComment(@RequestBody @Valid AddStudentCommentRequest studentComment) {
        return service.addStudentComment(studentComment);
    }

    @PostMapping("/payment")
    @Operation(summary = "Add information about the student's payment history.", description = "Provide the necessary data to create a new payment to a specific student.")
    public StudentPaymentInfoResponse addStudentPayment(@RequestBody @Valid  AddStudentPaymentRequest studentPayment) {
        return service.addStudentPayment(studentPayment);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a student by ID", description = "Provide an ID to delete a specific student.")
    public StudentDataResponse deleteStudent(@PathVariable Integer id) {
        return service.deleteStudent(id);
    }
}
