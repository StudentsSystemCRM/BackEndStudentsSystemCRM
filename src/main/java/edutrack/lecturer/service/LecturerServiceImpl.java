package edutrack.lecturer.service;

import edutrack.exception.ResourceAlreadyExistsException;
import edutrack.exception.ResourceNotFoundException;
import edutrack.group.entity.GroupEntity;
import edutrack.group.repository.GroupRepository;
import edutrack.lecturer.constant.LecturerStatus;
import edutrack.lecturer.dto.request.LecturerCreateRequest;
import edutrack.lecturer.dto.request.LecturerUpdateRequest;
import edutrack.lecturer.dto.response.LecturerDataResponse;
import edutrack.lecturer.entity.LecturerEntity;
import edutrack.lecturer.repository.LecturerRepository;
import edutrack.lecturer.util.EntityDtoLecturerMapper;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class LecturerServiceImpl implements LecturerService {

    LecturerRepository lecturerRepo;
    GroupRepository groupRepo;


    private LecturerDataResponse lecturerToLecturerDataResponse(LecturerEntity lecturer) {
        Set<Long> groupIds = lecturer.getGroups().stream()
                .map(GroupEntity::getId)
                .collect(Collectors.toSet());

        return new LecturerDataResponse(
                lecturer.getId(),
                lecturer.getFirstName(),
                lecturer.getLastName(),
                lecturer.getPhoneNumber(),
                lecturer.getEmail(),
                lecturer.getCity(),
                lecturer.getStatus(),
                groupIds
        );
    }

    @Override
    public LecturerDataResponse getLecturerById(Long id) {
        LecturerEntity lecturer = lecturerRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lecturer not found with id: " + id));
        return lecturerToLecturerDataResponse(lecturer);
    }

    @Override
    public List<LecturerDataResponse> findLecturersByLastName(String lastName) {
        return lecturerRepo.findByLastName(lastName).stream()
                .map(this::lecturerToLecturerDataResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<LecturerDataResponse> findLecturersByStatus(LecturerStatus status) {
        return lecturerRepo.findByStatus(status).stream()
                .map(this::lecturerToLecturerDataResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<LecturerDataResponse> findLecturersByCity(String city) {
        return lecturerRepo.findByCity(city).stream()
                .map(this::lecturerToLecturerDataResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<LecturerDataResponse> getAllLecturers() {
        List<LecturerEntity> lecturers = lecturerRepo.findAll();
        return lecturers.stream()
                .map(this::lecturerToLecturerDataResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public LecturerDataResponse createLecturer(LecturerCreateRequest request) {
        if (lecturerRepo.existsByEmail(request.getEmail())) {
            throw new ResourceAlreadyExistsException("Lecturer already exists with email: " + request.getEmail());
        }

        LecturerEntity lecturer = EntityDtoLecturerMapper.INSTANCE.toLecturerEntity(request);
        setGroupsForLecturer(lecturer, request.getGroupIds());

        LecturerEntity savedLecturer = lecturerRepo.save(lecturer);
        return lecturerToLecturerDataResponse(savedLecturer);
    }

    @Override
    @Transactional
    public LecturerDataResponse updateLecturer(LecturerUpdateRequest updateRequest) {
        LecturerEntity lecturer = lecturerRepo.findById(updateRequest.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Lecturer not found with id: " + updateRequest.getId()));

        // Проверка на уникальность email
        if (updateRequest.getEmail() != null && !updateRequest.getEmail().equals(lecturer.getEmail())) {
            if (lecturerRepo.existsByEmail(updateRequest.getEmail())) {
                throw new ResourceAlreadyExistsException("Lecturer already exists with email: " + updateRequest.getEmail());
            }
        }

        EntityDtoLecturerMapper.INSTANCE.updateLecturerFromRequest(updateRequest, lecturer);
        setGroupsForLecturer(lecturer, updateRequest.getGroupIds());

        lecturerRepo.save(lecturer);
        return lecturerToLecturerDataResponse(lecturer);
    }

    private void setGroupsForLecturer(LecturerEntity lecturer, Set<Long> groupIds) {
        if (groupIds != null && !groupIds.isEmpty()) {
            Set<GroupEntity> groups = new HashSet<>(groupRepo.findAllById(groupIds));
            if (groups.size() != groupIds.size()) {
                throw new ResourceNotFoundException("One or more groups not found");
            }
            lecturer.setGroups(groups);
        } else {
            lecturer.setGroups(Set.of()); // Если groupIds пустой, устанавливаем пустой набор
        }
    }

    @Override
    @Transactional
    public LecturerDataResponse deleteLecturer(Long id) {
        LecturerEntity lecturer = lecturerRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lecturer not found with id: " + id));

        LecturerDataResponse response = lecturerToLecturerDataResponse(lecturer);
        lecturerRepo.deleteById(id);
        return response;
    }
}
