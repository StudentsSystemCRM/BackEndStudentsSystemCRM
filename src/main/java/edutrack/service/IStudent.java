package edutrack.service;

import java.util.List;

import edutrack.dto.request.students.AddStudentCommentRequest;
import edutrack.dto.request.students.AddStudentPaymentRequest;
import edutrack.dto.request.students.StudentCreateRequest;
import edutrack.dto.request.students.StudentUpdateDataRequest;
import edutrack.dto.response.students.StudentActivityLogResponse;
import edutrack.dto.response.students.StudentDataResponse;
import edutrack.dto.response.students.StudentPaymentInfoResponse;

public interface IStudent {
	
	StudentDataResponse createStudent(StudentCreateRequest student);
	
	List<StudentDataResponse> getAllStudents();
	List<StudentDataResponse> getStudentsByName(String name);
	List<StudentDataResponse> getStudentsBySurname(String surname);
	List<StudentDataResponse> getStudentsByNameAndSurname(String name, String surname);
	StudentDataResponse getStudentById(Integer id);
	StudentActivityLogResponse getStudentActivityLog(Integer id);
	StudentPaymentInfoResponse getStudentPaymentInfo(Integer id);
	
	StudentDataResponse updateStudent(StudentUpdateDataRequest student);
	StudentActivityLogResponse  addStudentComment(AddStudentCommentRequest studentComment);
	StudentPaymentInfoResponse  addStudentPayment(AddStudentPaymentRequest studentPayment);
	
	StudentDataResponse deleteStudent(Integer id);
}
