package edutrack.service;

import edutrack.dto.request.students.AddStudentCommentRequest;
import edutrack.dto.request.students.AddStudentPaymentRequest;
import edutrack.dto.request.students.StudentCreateRequest;
import edutrack.dto.request.students.StudentUpdateDataRequest;
import edutrack.dto.response.students.StudentActivityLogResponse;
import edutrack.dto.response.students.StudentDataResponse;
import edutrack.dto.response.students.StudentPaymentInfoResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService implements IStudent{

    @Override
    public StudentDataResponse createStudent(StudentCreateRequest student) {
        return null;
    }

    @Override
    public List<StudentDataResponse> getAllStudents() {
        return List.of();
    }

    @Override
    public List<StudentDataResponse> getStudentsByName(String name) {
        return List.of();
    }

    @Override
    public List<StudentDataResponse> getStudentsBySurname(String surname) {
        return List.of();
    }

    @Override
    public List<StudentDataResponse> getStudentsByNameAndSurname(String name, String surname) {
        return List.of();
    }

    @Override
    public StudentDataResponse getStudentById(Integer id) {
        return null;
    }

    @Override
    public StudentActivityLogResponse getStudentActivityLog(Integer id) {
        return null;
    }

    @Override
    public StudentPaymentInfoResponse getStudentPaymentInfo(Integer id) {
        return null;
    }

    @Override
    public StudentDataResponse updateStudent(StudentUpdateDataRequest student) {
        return null;
    }

    @Override
    public StudentActivityLogResponse addStudentComment(AddStudentCommentRequest studentComment) {
        return null;
    }

    @Override
    public StudentPaymentInfoResponse addStudentPayment(AddStudentPaymentRequest studentPayment) {
        return null;
    }

    @Override
    public StudentDataResponse deleteStudent(Integer id) {
        return null;
    }
}
