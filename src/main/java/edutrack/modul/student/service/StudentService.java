package edutrack.modul.student.service;

import java.util.List;

import edutrack.modul.student.dto.request.StudentCreateRequest;
import edutrack.modul.student.dto.request.StudentUpdateDataRequest;
import edutrack.modul.student.dto.response.StudentDataResponse;

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
