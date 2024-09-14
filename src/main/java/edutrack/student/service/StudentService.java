package edutrack.student.service;

import java.util.List;

import edutrack.student.dto.request.StudentCreateRequest;
import edutrack.student.dto.request.StudentUpdateDataRequest;
import edutrack.student.dto.response.StudentDataResponse;

public interface StudentService {
	
	StudentDataResponse createStudent(StudentCreateRequest student);
	
	List<StudentDataResponse> getAllStudents();
	List<StudentDataResponse> getStudentsByName(String name);
	List<StudentDataResponse> getStudentsBySurname(String surname);
	List<StudentDataResponse> getStudentsByNameAndSurname(String name, String surname);
	StudentDataResponse getStudentById(Long id);
	StudentDataResponse updateStudent(StudentUpdateDataRequest student);
	StudentDataResponse deleteStudent(Long id);

	List<StudentDataResponse> getStudentsByGroup(String name);
}
