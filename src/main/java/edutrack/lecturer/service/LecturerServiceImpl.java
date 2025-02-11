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
import edutrack.student.exception.EmailAlreadyInUseException;
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

    LecturerRepository lecturerRepository;
    GroupRepository groupRepository;

    @Override
    public LecturerDataResponse getLecturerById(Long id) {
        LecturerEntity lecturer = lecturerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lecturer not found with id: " + id));
        return EntityDtoLecturerMapper.INSTANCE.toLecturerDataResponse(lecturer);
    }

    @Override
    public List<LecturerDataResponse> findLecturersByLastName(String lastName) {
        List<LecturerEntity> lecturers = lecturerRepository.findByLastName(lastName);
        return lecturers.stream()
                .map(EntityDtoLecturerMapper.INSTANCE::toLecturerDataResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<LecturerDataResponse> findLecturersByStatus(LecturerStatus status) {
        List<LecturerEntity> lecturers = lecturerRepository.findByStatus(status);
        return lecturers.stream()
                .map(EntityDtoLecturerMapper.INSTANCE::toLecturerDataResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<LecturerDataResponse> findLecturersByCity(String city) {
        List<LecturerEntity> lecturers = lecturerRepository.findByCity(city);
        return lecturers.stream()
                .map(EntityDtoLecturerMapper.INSTANCE::toLecturerDataResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<LecturerDataResponse> getAllLecturers() {
        List<LecturerEntity> lecturers = lecturerRepository.findAll();
        return lecturers.stream()
                .map(EntityDtoLecturerMapper.INSTANCE::toLecturerDataResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public LecturerDataResponse createLecturer(LecturerCreateRequest request) {

        if (lecturerRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyInUseException("Email already in use: " + request.getEmail());
        }

        Set<GroupEntity> groups = validateAndGetGroups(request.getGroupIds());
        LecturerEntity lecturer = EntityDtoLecturerMapper.INSTANCE.toLecturerEntity(request, groups);
        lecturer = lecturerRepository.save(lecturer);
        return EntityDtoLecturerMapper.INSTANCE.toLecturerDataResponse(lecturer);
    }

    @Override
    @Transactional
    public LecturerDataResponse updateLecturer(LecturerUpdateRequest request) {
        LecturerEntity lecturer = lecturerRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Lecturer not found with id: " + request.getId()));

        if (request.getEmail() != null) {
            if (!request.getEmail().equals(lecturer.getEmail()) && lecturerRepository.existsByEmail(request.getEmail())) {
                throw new EmailAlreadyInUseException("Email already in use: " + request.getEmail());
            }
            lecturer.setEmail(request.getEmail());
        }

        EntityDtoLecturerMapper.INSTANCE.updateLecturerFromRequest(request, lecturer);

        if (request.getGroupIds() != null && !request.getGroupIds().isEmpty()) {
            Set<GroupEntity> groups = validateAndGetGroups(request.getGroupIds());
            lecturer.setGroups(groups);
        }

        lecturer = lecturerRepository.save(lecturer);
        return EntityDtoLecturerMapper.INSTANCE.toLecturerDataResponse(lecturer);
    }

    private Set<GroupEntity> validateAndGetGroups(Set<Long> groupIds) {

        if (groupIds == null || groupIds.isEmpty()) {
            return new HashSet<>();
        }

        Set<GroupEntity> groups = new HashSet<>(groupRepository.findAllById(groupIds));
        if (groups.size() != groupIds.size()) {
            Set<Long> existingGroupIds = groups.stream()
                    .map(GroupEntity::getId)
                    .collect(Collectors.toSet());
            for (Long groupId : groupIds) {
                if (!existingGroupIds.contains(groupId)) {
                    throw new ResourceNotFoundException("Group not found with id: " + groupId);
                }
            }
        }
        return groups;
    }

    @Override
    @Transactional
    public LecturerDataResponse deleteLecturer(Long id) {
        LecturerEntity lecturer = lecturerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lecturer not found with id: " + id));

        LecturerDataResponse response = EntityDtoLecturerMapper.INSTANCE.toLecturerDataResponse(lecturer);
        lecturerRepository.deleteById(id);
        return response;
    }
}
