package edutrack.controller;

import edutrack.dto.request.students.AddStudentCommentRequest;
import edutrack.dto.request.students.AddStudentPaymentRequest;
import edutrack.dto.request.students.StudentCreateRequest;
import edutrack.dto.request.students.StudentUpdateDataRequest;
import edutrack.dto.response.students.StudentActivityLogResponse;
import edutrack.dto.response.students.StudentDataResponse;
import edutrack.dto.response.students.StudentPaymentInfoResponse;
import edutrack.service.IStudent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController{
    @Autowired
    IStudent service;

    @PostMapping
    public StudentDataResponse createStudent(@RequestBody StudentCreateRequest student) {
        return service.createStudent(student);
    }

    @GetMapping
    public List<StudentDataResponse> getAllStudents() {
        return service.getAllStudents();
    }

    @GetMapping("/name")
    public List<StudentDataResponse> getStudentsByName(@RequestParam String name) {
        return service.getStudentsByName(name);
    }

    @GetMapping("/surname")
    public List<StudentDataResponse> getStudentsBySurname(@RequestParam String surname) {
        return service.getStudentsBySurname(surname);
    }

    @GetMapping("/name_and_surname")
    public List<StudentDataResponse> getStudentsByNameAndSurname(@RequestParam String name, @RequestParam String surname) {
        return service.getStudentsByNameAndSurname(name, surname);
    }

    @GetMapping("/{id}")
    public StudentDataResponse getStudentById(@PathVariable Integer id) {
        return service.getStudentById(id);
    }

    @GetMapping("/{id}/activity")
    public StudentActivityLogResponse getStudentActivityLog(@PathVariable Integer id) {
        return service.getStudentActivityLog(id);
    }

    @GetMapping("/{id}/payments")
    public StudentPaymentInfoResponse getStudentPaymentInfo(@PathVariable Integer id) {
        return service.getStudentPaymentInfo(id);
    }

    @PutMapping
    public StudentDataResponse updateStudent(@RequestBody StudentUpdateDataRequest student) {
        return service.updateStudent(student);
    }

    @PostMapping("/comment")
    public StudentActivityLogResponse addStudentComment(@RequestBody AddStudentCommentRequest studentComment) {
        return service.addStudentComment(studentComment);
    }

    @PostMapping("/payment")
    public StudentPaymentInfoResponse addStudentPayment(@RequestBody AddStudentPaymentRequest studentPayment) {
        return service.addStudentPayment(studentPayment);
    }

    @DeleteMapping("/{id}")
    public StudentDataResponse deleteStudent(@PathVariable Integer id) {
        return service.deleteStudent(id);
    }
}
