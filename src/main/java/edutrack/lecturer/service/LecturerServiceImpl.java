package edutrack.lecturer.service;

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

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class LecturerServiceImpl implements LecturerService {

    private final LecturerRepository lecturerRepo;
    private final GroupRepository groupRepo;

    @Override
    @Transactional
    public void addGroupsToLecturer(Long lecturerId, Set<String> groupNames) {
        LecturerEntity lecturer = lecturerRepo.findById(lecturerId)
                .orElseThrow(() -> new ResourceNotFoundException("Lecturer not found with id: " + lecturerId));

        Set<GroupEntity> groups = groupNames.stream()
                .map(groupName -> groupRepo.findByName(groupName))
                .filter(group -> group != null)
                .collect(Collectors.toSet());

        lecturer.getGroups().addAll(groups);
        lecturerRepo.save(lecturer);
    }

    @Override
    public LecturerDataResponse getLecturerById(Long id) {
        LecturerEntity lecturer = lecturerRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Lecturer not found"));
        return EntityDtoLecturerMapper.INSTANCE.lecturerToLecturerDataResponse(lecturer);
    }

    @Override
    public List<LecturerDataResponse> findLecturersByStatus(LecturerStatus status) {
        return lecturerRepo.findByStatus(status).stream()
                .map(EntityDtoLecturerMapper.INSTANCE::lecturerToLecturerDataResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<LecturerDataResponse> findLecturersByCity(String city) {
        return lecturerRepo.findByCity(city).stream()
                .map(EntityDtoLecturerMapper.INSTANCE::lecturerToLecturerDataResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public LecturerDataResponse createLecturer(LecturerCreateRequest request) {
        LecturerEntity lecturer = EntityDtoLecturerMapper.INSTANCE.lecturerCreateRequestToLecturer(request);
        Set<GroupEntity> groups = mapGroupNamesToEntities(request.getGroups());
        lecturer.setGroups(groups);
        LecturerEntity savedLecturer = lecturerRepo.save(lecturer);
        return EntityDtoLecturerMapper.INSTANCE.lecturerToLecturerDataResponse(savedLecturer);
    }

    private Set<GroupEntity> mapGroupNamesToEntities(Set<String> groupNames) {
        return groupNames.stream()
                .map(groupName -> groupRepo.findByName(groupName))
                .filter(group -> group != null)
                .collect(Collectors.toSet());
    }

    @Override
    public List<LecturerDataResponse> getAllLecturers() {
        List<LecturerEntity> lecturers = lecturerRepo.findAll();
        return lecturers.stream()
                .map(EntityDtoLecturerMapper.INSTANCE::lecturerToLecturerDataResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public LecturerDataResponse updateLecturer(LecturerUpdateRequest updateRequest) {


        LecturerEntity lecturer = lecturerRepo.findById(updateRequest.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Lecturer not found with id: " + updateRequest.getId()));

        EntityDtoLecturerMapper.INSTANCE.updateLecturerFromRequest(updateRequest, lecturer);

        if (updateRequest.getGroups() != null) {
            Set<GroupEntity> updatedGroups = mapGroupNamesToEntities(updateRequest.getGroups());
            lecturer.getGroups().clear();
            lecturer.getGroups().addAll(updatedGroups);

        }


        LecturerEntity updatedLecturer = lecturerRepo.save(lecturer);


        return EntityDtoLecturerMapper.INSTANCE.lecturerToLecturerDataResponse(updatedLecturer);
    }

    @Override
    @Transactional
    public LecturerDataResponse deleteLecturer(Long id) {
        LecturerEntity lecturer = lecturerRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lecturer not found with id: " + id));

        LecturerDataResponse response = EntityDtoLecturerMapper.INSTANCE.lecturerToLecturerDataResponse(lecturer);
        lecturerRepo.deleteById(id);

        return response;
    }
}
