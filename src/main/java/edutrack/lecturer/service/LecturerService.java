package edutrack.lecturer.service;

import edutrack.lecturer.dto.request.LecturerCreateRequest;
import edutrack.lecturer.dto.response.LecturerDataResponse;
import edutrack.lecturer.entity.LecturerEntity;

import java.util.List;

public interface LecturerService {
    LecturerDataResponse getLecturerById(Long id);
    LecturerDataResponse createLecturer(LecturerCreateRequest request);  // Возвращаем `LecturerDataResponse`
    List<LecturerDataResponse> getAllLecturers();
    LecturerDataResponse updateLecturer(Long id, LecturerCreateRequest request);
    void deleteLecturer(Long id);
}
