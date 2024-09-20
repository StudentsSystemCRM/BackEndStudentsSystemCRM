package edutrack.lecturer.service;

import edutrack.group.entity.GroupEntity;
import edutrack.lecturer.constant.LecturerStatus;
import edutrack.lecturer.dto.request.LecturerCreateRequest;
import edutrack.lecturer.dto.request.LecturerUpdateRequest;
import edutrack.lecturer.dto.response.LecturerDataResponse;
import edutrack.lecturer.entity.LecturerEntity;

import java.util.List;
import java.util.Set;

public interface LecturerService {

    void addGroupsToLecturer(Long lecturerId, Set<String> groupNames);

    LecturerDataResponse getLecturerById(Long id);
    List<LecturerDataResponse> findLecturersByStatus(LecturerStatus status);
    List<LecturerDataResponse> findLecturersByCity(String city);
    LecturerDataResponse createLecturer(LecturerCreateRequest request);
    List<LecturerDataResponse> getAllLecturers();
    LecturerDataResponse updateLecturer(LecturerUpdateRequest request);
    LecturerDataResponse deleteLecturer(Long id);

}
