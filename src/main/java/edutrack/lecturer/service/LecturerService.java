package edutrack.lecturer.service;

import edutrack.lecturer.constant.LecturerStatus;
import edutrack.lecturer.dto.request.LecturerCreateRequest;
import edutrack.lecturer.dto.request.LecturerUpdateRequest;
import edutrack.lecturer.dto.response.LecturerDataResponse;
import java.util.List;

public interface LecturerService {
    LecturerDataResponse getLecturerById(Long id);
    List<LecturerDataResponse> findLecturersByLastName(String lastName);
    List<LecturerDataResponse> findLecturersByStatus(LecturerStatus status);
    List<LecturerDataResponse> findLecturersByCity(String city);
    List<LecturerDataResponse> getAllLecturers ();
    LecturerDataResponse createLecturer(LecturerCreateRequest request);
    LecturerDataResponse updateLecturer(LecturerUpdateRequest request);
    LecturerDataResponse deleteLecturer(Long id);

}
