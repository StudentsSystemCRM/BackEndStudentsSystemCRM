package edutrack.modul.student.service;

import java.util.List;

import edutrack.modul.activityLog.dto.request.AddActivityLogRequest;
import edutrack.modul.activityLog.dto.response.StudentActivityLogResponse;
import edutrack.modul.payment.dto.request.AddPaymentRequest;
import edutrack.modul.payment.dto.response.StudentPaymentInfoResponse;
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
	StudentActivityLogResponse getStudentActivityLog(Long id);
	StudentPaymentInfoResponse getStudentPaymentInfo(Long id);
	
	StudentDataResponse updateStudent(StudentUpdateDataRequest student);
	StudentActivityLogResponse  addStudentComment(AddActivityLogRequest studentComment);
	StudentPaymentInfoResponse addStudentPayment(AddPaymentRequest studentPayment);
	
	StudentDataResponse deleteStudent(Long id);
}
