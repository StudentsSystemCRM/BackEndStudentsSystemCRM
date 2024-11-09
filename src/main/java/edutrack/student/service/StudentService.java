package edutrack.student.service;

import java.util.List;

import org.springframework.data.domain.Pageable;

import edutrack.student.constant.LeadStatus;
import edutrack.student.dto.request.StudentCreateRequest;
import edutrack.student.dto.request.StudentUpdateDataRequest;
import edutrack.student.dto.response.StudentDataResponse;

public interface StudentService {
	
	StudentDataResponse createStudent(StudentCreateRequest student);
	StudentDataResponse updateStudent(StudentUpdateDataRequest student);
	StudentDataResponse getStudentById(Long id);
	
	List<StudentDataResponse> getAllStudents(Pageable pageable);
	List<StudentDataResponse> getStudentsByName(Pageable pageable, String name);// containing, ignore case
	List<StudentDataResponse> getStudentsBySurname(Pageable pageable, String surname);// containing, ignore case
	List<StudentDataResponse> getStudentsByNameAndSurname(Pageable pageable, String name, String surname);// containing, ignore case
	List<StudentDataResponse> getStudentsByStatus(Pageable pageable, LeadStatus status);
	List<StudentDataResponse> getStudentsByStudentsIds(List<Long> ids);
	List<Long> getStudentGroupsIds(Long id);
	
	Boolean deleteStudent(Long id);
	
	Boolean addStudentToGroup(Long studentId, Long groupId);
	Boolean deleteStudentFromGroup(Long studentId, Long groupId);
	Boolean changeStudentGroup(Long studentId, Long groupId, Long oldGroupId);
}
