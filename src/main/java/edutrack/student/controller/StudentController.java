package edutrack.student.controller;

import java.util.List;

import edutrack.group.dto.response.GroupDataResponse;
import edutrack.student.dto.request.StudentCreateRequest;
import edutrack.student.dto.request.StudentUpdateDataRequest;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import jakarta.validation.constraints.Min;

@RestController
@RequestMapping("/api/students")
public class StudentController {
    private final StudentService service;
    public StudentController(StudentService service) {
        this.service = service;
    }
	
    @PostMapping("/create_student")
    @Operation(summary = "Create new student", description = "Provide necessary data to create a new student.")
    public StudentDataResponse createStudent(@RequestBody @Valid StudentCreateRequest student) {
        return service.createStudent(student);
    }
    
    @PutMapping("/update_student_information")
    @Operation(summary = "Update student information.", description = "Provide all data specific student and updated a needed fields.")
    public StudentDataResponse updateStudent(@RequestBody @Valid StudentUpdateDataRequest student) {
        return service.updateStudent(student);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get a student by ID", description = "Provide an ID to lookup a specific student.")
    public StudentDataResponse getStudentById(@PathVariable Long id) {
        return service.getStudentById(id);
    }
    
	@GetMapping
	@Operation(summary = "Get all students", description = "Retrieve a list of all students.")
	public  List<StudentDataResponse> getAllStudents(@RequestParam(required = false, defaultValue = "0") int page,
			@RequestParam(required = false, defaultValue = "20") int size,
			@RequestParam(required = false, defaultValue = "name") String sortBy,
			@RequestParam(required = false, defaultValue = "true") boolean ascending) {
		Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
		Pageable pageable = PageRequest.of(page, size, sort);
		return service.getAllStudents(pageable);
	}
	
    @GetMapping("/name")
    @Operation(summary = "Get a student by name", description = "Provide necessary a name of student.")
    public List<StudentDataResponse> getStudentsByName(@RequestParam(required = false, defaultValue = "0") int page,
	@RequestParam(required = false, defaultValue = "20") int size, @RequestParam String name) {
    	Pageable pageable = PageRequest.of(page, size);
        return service.getStudentsByName(pageable,name);
    }

    @GetMapping("/surname")
    @Operation(summary = "Get a student by surname", description = "Provide necessary a surname of student.")
    public List<StudentDataResponse> getStudentsBySurname(@RequestParam(required = false, defaultValue = "0") int page,
    		@RequestParam(required = false, defaultValue = "20") int size, @RequestParam String surname) {
    	Pageable pageable = PageRequest.of(page, size);
        return service.getStudentsBySurname(pageable,surname);
    }

    @GetMapping("/name_and_surname")
    @Operation(summary = "Get a student by name and surname", description = "Provide necessary a name and surname of student.")
    public List<StudentDataResponse> getStudentsByNameAndSurname(@RequestParam(required = false, defaultValue = "0") int page,
    		@RequestParam(required = false, defaultValue = "20") int size, @RequestParam String name, @RequestParam String surname) {
    	Pageable pageable = PageRequest.of(page, size);
        return service.getStudentsByNameAndSurname(pageable,name, surname);
    }
    
	@GetMapping("/students/{ids}")
	@Operation(summary = "Get students of a group by students ids", description = "Returns a list of students that matches the given list of students ids.")
	public List<StudentDataResponse> getStudentsByStudentsIds(@PathVariable List<Long> ids) {
		return service.getStudentsByStudentsIds(ids);
	}
	
	@GetMapping("/student/{id}")
	@Operation(summary = "Get groups ids by student id", description = "Returns a list of groups ids that matches the given student id.")
	public List<Long> getStudentGroupsIds(@PathVariable Long id) {
		return service.getStudentGroupsIds(id);
	}
	
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a student by ID", description = "Provide an ID to delete a specific student.")
    public Boolean deleteStudent(@PathVariable Long id) {
        return service.deleteStudent(id);
    }
    
	@PostMapping("/add-student")
	@Operation(summary = "Add a student to a group", description = "Adds a student to the specified group by student ID and group ID.")
	public Boolean addStudentToGroup(@RequestParam @Min(0) Long studentId, @RequestParam @Min(0) Long groupId) {
		return service.addStudentToGroup(studentId, groupId);
	}
	
	@DeleteMapping("/remove-student")
	@Operation(summary = "Remove a student from a group", description = "Removes a student from the specified group by student ID and group ID.")
	public Boolean deleteStudentFromGroup(@RequestParam @Min(0) Long studentId, @RequestParam @Min(0) Long groupId) {
		return service.deleteStudentFromGroup(studentId, groupId);
	}
	
	@PutMapping("/{studentId}/{groupId}/{oldGroupId}")
	@Operation(summary = "Change student's group", description = "Updates the group of a student to a new one.")
	public void changeStudentGroup(@PathVariable Long studentId, @PathVariable Long groupId,
			@PathVariable Long oldGroupId) {
		service.changeStudentGroup(studentId, groupId, oldGroupId);
	}
}
