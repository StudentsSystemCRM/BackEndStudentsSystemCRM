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
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class LecturerServiceImpl implements LecturerService {

    LecturerRepository lecturerRepo;
    GroupRepository groupRepo;

    @Override
    public LecturerDataResponse getLecturerById(Long id) {
        LecturerEntity lecturer = lecturerRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lecturer not found with id: " + id));
        return convertToResponse(lecturer);
    }
    @Override
    public List<LecturerDataResponse> findLecturersByLastName(String lastName) {
        return lecturerRepo.findByLastName(lastName).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    @Override
    public List<LecturerDataResponse> findLecturersByStatus(LecturerStatus status) {
        return lecturerRepo.findByStatus(status).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<LecturerDataResponse> findLecturersByCity(String city) {
        return lecturerRepo.findByCity(city).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    @Override
    public List<LecturerDataResponse> getAllLecturers() {
        List<LecturerEntity> lecturers = lecturerRepo.findAll();
        return lecturers.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public LecturerDataResponse createLecturer(LecturerCreateRequest request) {
        if (lecturerRepo.existsByEmail(request.getEmail())) {
            throw new ResourceAlreadyExistsException("Lecturer already exists with email: " + request.getEmail());
        }

        LecturerEntity lecturer = new LecturerEntity();
        lecturer.setFirstName(request.getFirstName());
        lecturer.setLastName(request.getLastName());
        lecturer.setPhoneNumber(request.getPhoneNumber());
        lecturer.setEmail(request.getEmail());
        lecturer.setCity(request.getCity());
        lecturer.setStatus(request.getStatus());

        Set<GroupEntity> groups = mapGroupNamesToEntities(request.getGroups());
        lecturer.setGroups(groups);

        LecturerEntity savedLecturer = lecturerRepo.save(lecturer);
        return convertToResponse(savedLecturer);
    }


    private Set<GroupEntity> mapGroupNamesToEntities(Set<String> groupNames) {
        return groupNames.stream()
                .map(this::findGroupByNameOrThrow)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional
    public LecturerDataResponse updateLecturer(LecturerUpdateRequest updateRequest) {

        LecturerEntity lecturer = lecturerRepo.findById(updateRequest.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Lecturer not found with id: " + updateRequest.getId()));

        if (updateRequest.getEmail() != null && lecturerRepo.existsByEmail(updateRequest.getEmail())) {
            throw new ResourceAlreadyExistsException("Lecturer already exists with email: " + updateRequest.getEmail());
        }

        if (updateRequest.getFirstName() != null) {
            lecturer.setFirstName(updateRequest.getFirstName());
        }
        if (updateRequest.getLastName() != null) {
            lecturer.setLastName(updateRequest.getLastName());
        }
        if (updateRequest.getPhoneNumber() != null) {
            lecturer.setPhoneNumber(updateRequest.getPhoneNumber());
        }
        if (updateRequest.getEmail() != null) {
            lecturer.setEmail(updateRequest.getEmail());
        }
        if (updateRequest.getCity() != null) {
            lecturer.setCity(updateRequest.getCity());
        }
        if (updateRequest.getStatus() != null) {
            lecturer.setStatus(updateRequest.getStatus());
        }

        if (updateRequest.getGroups() != null) {
            Set<GroupEntity> groups = mapGroupNamesToEntities(updateRequest.getGroups());
            lecturer.setGroups(groups);
        }

        lecturerRepo.save(lecturer);

        return convertToResponse(lecturer);
    }


    @Override
    @Transactional
    public LecturerDataResponse deleteLecturer(Long id) {
        LecturerEntity lecturer = lecturerRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lecturer not found with id: " + id));

        LecturerDataResponse response = convertToResponse(lecturer);
        lecturerRepo.deleteById(id);
        return response;
    }


    private GroupEntity findGroupByNameOrThrow(String groupName) {
        GroupEntity group = groupRepo.findByName(groupName);
        if (group == null) {
            throw new ResourceNotFoundException("Group not found with name: " + groupName);
        }
        return group;
    }
    private LecturerDataResponse convertToResponse(LecturerEntity lecturer) {
        LecturerDataResponse response = new LecturerDataResponse();
        response.setId(lecturer.getId());
        response.setFirstName(lecturer.getFirstName());
        response.setLastName(lecturer.getLastName());
        response.setPhoneNumber(lecturer.getPhoneNumber());
        response.setEmail(lecturer.getEmail());
        response.setCity(lecturer.getCity());
        response.setStatus(lecturer.getStatus());
        response.setGroupNames(lecturer.getGroups().stream()
                .map(GroupEntity::getName)
                .collect(Collectors.toSet()));
        return response;
    }
}
