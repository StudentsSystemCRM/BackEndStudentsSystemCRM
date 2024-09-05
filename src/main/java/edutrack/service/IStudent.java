package edutrack.service;

import java.util.List;

import edutrack.dto.request.activityLog.AddActivityLogRequest;
import edutrack.dto.request.payment.AddPaymentRequest;
import edutrack.dto.request.student.StudentCreateRequest;
import edutrack.dto.request.student.StudentUpdateDataRequest;
import edutrack.dto.response.activityLog.StudentActivityLogResponse;
import edutrack.dto.response.payment.StudentPaymentInfoResponse;
import edutrack.dto.response.student.StudentDataResponse;

public interface IStudent {
	
	StudentDataResponse createStudent(StudentCreateRequest student);
	
	List<StudentDataResponse> getAllStudents();
	List<StudentDataResponse> getStudentsByName(String name);
	List<StudentDataResponse> getStudentsBySurname(String surname);
	List<StudentDataResponse> getStudentsByNameAndSurname(String name, String surname);
	StudentDataResponse getStudentById(Long id);
	StudentActivityLogResponse getStudentActivityLog(Long id);
	StudentPaymentInfoResponse getStudentPaymentInfo(Long id);
	
	StudentDataResponse updateStudent(StudentUpdateDataRequest student);
	StudentActivityLogResponse  addStudentComment(AddActivityLogRequest studentComment);
	StudentPaymentInfoResponse addStudentPayment(AddPaymentRequest studentPayment);
	
	StudentDataResponse deleteStudent(Long id);
}
