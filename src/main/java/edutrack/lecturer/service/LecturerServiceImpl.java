package edutrack.lecturer.service;

import edutrack.lecturer.dto.request.LecturerCreateRequest;
import edutrack.lecturer.dto.response.LecturerDataResponse;
import edutrack.lecturer.entity.LecturerEntity;
import edutrack.lecturer.repository.LecturerRepository;
import edutrack.lecturer.util.EntityDtoLecturerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LecturerServiceImpl implements LecturerService {

    private final LecturerRepository lecturerRepo;
    private final EntityDtoLecturerMapper entityDtoLecturerMapper;

    @Override
    public LecturerDataResponse getLecturerById(Long id) {
        LecturerEntity lecturer = lecturerRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Lecturer not found"));
        return convertToResponse(lecturer);
    }

    @Override
    public LecturerDataResponse createLecturer(LecturerCreateRequest request) {
        LecturerEntity lecturer = entityDtoLecturerMapper.lecturerCreateRequestToLecturer(request);
        LecturerEntity savedLecturer = lecturerRepo.save(lecturer);
        return convertToResponse(savedLecturer);
    }

    @Override
    public List<LecturerDataResponse> getAllLecturers() {
        return lecturerRepo.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public LecturerDataResponse updateLecturer(Long id, LecturerCreateRequest request) {
        LecturerEntity lecturer = lecturerRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Lecturer not found"));

        // Обновление полей лектора
        lecturer.setFirstName(request.getFirstName());
        lecturer.setLastName(request.getLastName());
        lecturer.setPhoneNumber(request.getPhoneNumber());
        lecturer.setEmail(request.getEmail());
        lecturer.setCity(request.getCity());
        lecturer.setStatus(request.getStatus());
        lecturer.setGroups(request.getGroups());

        LecturerEntity updatedLecturer = lecturerRepo.save(lecturer);
        return convertToResponse(updatedLecturer);
    }

    @Override
    public void deleteLecturer(Long id) {
        lecturerRepo.deleteById(id);
    }

    private LecturerDataResponse convertToResponse(LecturerEntity entity) {
        return entityDtoLecturerMapper.lecturerToLecturerDataResponse(entity);
    }
}
