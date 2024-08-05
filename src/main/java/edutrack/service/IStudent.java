package edutrack.service;

import java.util.List;

import edutrack.dto.request.AddStudentCommentRequest;
import edutrack.dto.request.AddStudentPaymentRequest;
import edutrack.dto.request.StudentCreateRequest;
import edutrack.dto.request.StudentUpdateDataRequest;
import edutrack.dto.response.StudentActivityLogResponce;
import edutrack.dto.response.StudentDataResponce;
import edutrack.dto.response.StudentPaymentInfoResponce;

public interface IStudent {
	
	StudentDataResponce createStudent(StudentCreateRequest student);
	
	List<StudentDataResponce> getAllStudents();
	List<StudentDataResponce> getStudentsByName(String name);
	List<StudentDataResponce> getStudentsBySurname(String surname);
	List<StudentDataResponce> getStudentsByNameAndSurname(String name, String surname);
	StudentDataResponce getStudentById(Integer id);
	StudentActivityLogResponce getStudentActivityLog(Integer id);
	StudentPaymentInfoResponce getStudentPaymentInfo(Integer id);
	
	StudentDataResponce updateStudent(StudentUpdateDataRequest student);
	StudentActivityLogResponce  addStudentComment(AddStudentCommentRequest studentComment);
	StudentPaymentInfoResponce  addStudentPayment(AddStudentPaymentRequest studentpayment);
	
	StudentDataResponce deleteStudent(Integer id);
	
	
	
	
	
	
}
