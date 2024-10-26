package edutrack.schedule.service;

import edutrack.group.entity.GroupEntity;
import edutrack.group.exception.GroupNotFoundException;
import edutrack.group.repository.GroupRepository;
import edutrack.schedule.constant.SheduleType;
import edutrack.schedule.dto.request.ScheduleCreateRequest;
import edutrack.schedule.dto.request.ScheduleUpdateDataRequest;
import edutrack.schedule.dto.response.ScheduleResponse;
import edutrack.schedule.dto.response.SingleScheduleResponse;
import edutrack.schedule.entity.GroupScheduleEntity;
import edutrack.schedule.entity.StudentScheduleEntity;
import edutrack.schedule.exception.ScheduleNotFoundException;
import edutrack.schedule.repository.GroupScheduleRepository;
import edutrack.schedule.util.EntityDtoScheduleMapper;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class GroupScheduleService implements ScheduleService {

	GroupScheduleRepository groupScheduleRepo;
	GroupRepository groupRepo;
	
    private GroupScheduleEntity findScheduleById(Long scheduleId) {
        return groupScheduleRepo.findById(scheduleId).orElseThrow(() -> new ScheduleNotFoundException("Schedule with id " + scheduleId + " not found"));
    }
    
    private List<GroupScheduleEntity> findShedulersByGroupId(Long id) {
		List<GroupScheduleEntity> scheduleEntities = groupScheduleRepo.findByGroupId(id);
        return (scheduleEntities.isEmpty() || scheduleEntities == null) ? new ArrayList<>() : scheduleEntities;
    }
    
	private List<GroupScheduleEntity> findSchedulersBySheduleType(SheduleType sheduleType) {
		List<GroupScheduleEntity> scheduleEntities = groupScheduleRepo.findAllBySheduleType(sheduleType);
		return (scheduleEntities.isEmpty() || scheduleEntities == null) ? new ArrayList<>() : scheduleEntities;
	}
    
    private GroupEntity findGroupById(Long id) {
        return groupRepo.findById(id).orElseThrow(() -> new GroupNotFoundException("Group with id " + id + " not found"));
    }
    
	@Override
	public ScheduleResponse getSchedule(Long scheduleId) {
		GroupScheduleEntity groupScheduleEntity = findScheduleById(scheduleId);
		Long id = groupScheduleEntity.getGroup().getId();
		List<SingleScheduleResponse> response = new ArrayList<>();
		response.add(EntityDtoScheduleMapper.INSTANCE.groupScheduleEntityToSingleScheduleResponse(groupScheduleEntity));
		return EntityDtoScheduleMapper.INSTANCE.singleScheduleResponseToSheduleResponse(id, response);
	}
	
	@Override
	public ScheduleResponse getAllSchedulers(Long id) {
		List<SingleScheduleResponse> groupScheduleEntities = findShedulersByGroupId(id).stream().map(EntityDtoScheduleMapper.INSTANCE::groupScheduleEntityToSingleScheduleResponse)
				.collect(Collectors.toList());
		return EntityDtoScheduleMapper.INSTANCE.singleScheduleResponseToSheduleResponse(id, groupScheduleEntities);
	}
	
	@Override
	public List<SingleScheduleResponse> getSchedulersBySheduleType(SheduleType sheduleType) {
		return findSchedulersBySheduleType(sheduleType).stream().map(EntityDtoScheduleMapper.INSTANCE::groupScheduleEntityToSingleScheduleResponse)
				.collect(Collectors.toList());
	}
	
	@Override
	@Transactional
	public ScheduleResponse addSchedule(ScheduleCreateRequest scheduleCreateRequest) {	
		Long id = scheduleCreateRequest.getId();
		GroupEntity groupEntity = findGroupById(id);
		GroupScheduleEntity groupScheduleEntity = EntityDtoScheduleMapper.INSTANCE.groupScheduleCreateRequestToGroupScheduleEntity(scheduleCreateRequest);
		groupScheduleEntity.setGroup(groupEntity);
		groupScheduleRepo.save(groupScheduleEntity);
		List<SingleScheduleResponse> groupScheduleEntities = findShedulersByGroupId(id).stream().map(EntityDtoScheduleMapper.INSTANCE::groupScheduleEntityToSingleScheduleResponse)
				.collect(Collectors.toList());
		return EntityDtoScheduleMapper.INSTANCE.singleScheduleResponseToSheduleResponse(id, groupScheduleEntities);
	}
	
	@Override
	@Transactional
	public ScheduleResponse updateSchedule(ScheduleUpdateDataRequest scheduleUpdateDataRequest) {
		Long id = scheduleUpdateDataRequest.getId();
		GroupEntity groupEntity = findGroupById(id);
		GroupScheduleEntity groupScheduleEntity = findScheduleById(scheduleUpdateDataRequest.getScheduleId());
		if (scheduleUpdateDataRequest.getSheduleType() != null) {
			groupScheduleEntity.setSheduleType(scheduleUpdateDataRequest.getSheduleType());
		}
		if (scheduleUpdateDataRequest.getSendDate() != null) {
			groupScheduleEntity.setSendDate(scheduleUpdateDataRequest.getSendDate());
		}
		if (scheduleUpdateDataRequest.getSubject() != null) {
			groupScheduleEntity.setSubject(scheduleUpdateDataRequest.getSubject());
		}
		if (scheduleUpdateDataRequest.getMessage() != null) {
			groupScheduleEntity.setMessage(scheduleUpdateDataRequest.getMessage());
		}
		groupScheduleEntity.setLastModifiedDate(ZonedDateTime.now());
		groupScheduleEntity.setGroup(groupEntity);
		groupScheduleRepo.save(groupScheduleEntity);
		List<SingleScheduleResponse> groupScheduleEntities = findShedulersByGroupId(id).stream().map(EntityDtoScheduleMapper.INSTANCE::groupScheduleEntityToSingleScheduleResponse)
				.collect(Collectors.toList());
		return EntityDtoScheduleMapper.INSTANCE.singleScheduleResponseToSheduleResponse(id, groupScheduleEntities);
	}

	@Override
	@Transactional
	public Boolean deleteSchedule(Long scheduleId) {
		groupScheduleRepo.deleteById(findScheduleById(scheduleId).getScheduleId());
		return true;
	}

	@Override
	@Transactional
	public Boolean deleteAllSchedulers(Long id) {
		findShedulersByGroupId(id).forEach(schedule -> groupScheduleRepo.deleteById(schedule.getScheduleId()));
		return true;
	}

}
