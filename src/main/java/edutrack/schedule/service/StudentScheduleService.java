package edutrack.schedule.service;

import edutrack.exception.StudentNotFoundException;
import edutrack.student.entity.StudentEntity;
import edutrack.student.repository.StudentRepository;
import edutrack.schedule.constant.SheduleType;
import edutrack.schedule.dto.request.ScheduleCreateRequest;
import edutrack.schedule.dto.request.ScheduleUpdateDataRequest;
import edutrack.schedule.dto.response.ScheduleResponse;
import edutrack.schedule.dto.response.SingleScheduleResponse;
import edutrack.schedule.entity.StudentScheduleEntity;
import edutrack.schedule.exception.ScheduleNotFoundException;
import edutrack.schedule.repository.StudentScheduleRepository;
import edutrack.schedule.util.EntityDtoScheduleMapper;
import jakarta.transaction.Transactional;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class StudentScheduleService implements ScheduleService {

	StudentScheduleRepository studentScheduleRepo;
	StudentRepository studentRepo;
	
    private StudentScheduleEntity findScheduleById(Long scheduleId) {
        return studentScheduleRepo.findById(scheduleId).orElseThrow(() -> new ScheduleNotFoundException("Schedule with id " + scheduleId + " not found"));
    }
    
    private List<StudentScheduleEntity> findShedulersByStudentId(Long id) {
		List<StudentScheduleEntity> scheduleEntities = studentScheduleRepo.findByStudentId(id);
        return (scheduleEntities.isEmpty() || scheduleEntities == null) ? new ArrayList<>() : scheduleEntities;
    }
    
	private List<StudentScheduleEntity> findSchedulersBySheduleType(SheduleType sheduleType) {
		List<StudentScheduleEntity> scheduleEntities = studentScheduleRepo.findAllBySheduleType(sheduleType);
		return (scheduleEntities.isEmpty() || scheduleEntities == null) ? new ArrayList<>() : scheduleEntities;
	}
	
    private StudentEntity findStudentById(Long id) {
        return studentRepo.findById(id).orElseThrow(() -> new StudentNotFoundException("Student with id " + id + " not found"));
    }
    
	@Override
	public ScheduleResponse getSchedule(Long scheduleId) {
		StudentScheduleEntity studentScheduleEntity = findScheduleById(scheduleId);
		Long id = studentScheduleEntity.getStudent().getId();
		List<SingleScheduleResponse> response = new ArrayList<>();
		response.add(EntityDtoScheduleMapper.INSTANCE.studentScheduleEntityToSingleScheduleResponse(studentScheduleEntity));
		return EntityDtoScheduleMapper.INSTANCE.singleScheduleResponseToSheduleResponse(id, response);
	}
	
	@Override
	public ScheduleResponse getAllSchedulers(Long id) {
		List<SingleScheduleResponse> studentScheduleResponse = findShedulersByStudentId(id).stream().map(EntityDtoScheduleMapper.INSTANCE::studentScheduleEntityToSingleScheduleResponse)
				.collect(Collectors.toList());
		return EntityDtoScheduleMapper.INSTANCE.singleScheduleResponseToSheduleResponse(id, studentScheduleResponse);
	}
	
	@Override
	public List<SingleScheduleResponse> getSchedulersBySheduleType(SheduleType sheduleType) {
		return findSchedulersBySheduleType(sheduleType).stream().map(EntityDtoScheduleMapper.INSTANCE::studentScheduleEntityToSingleScheduleResponse)
		.collect(Collectors.toList());
	}
	
	@Override
	@Transactional
	public ScheduleResponse addSchedule(ScheduleCreateRequest scheduleCreateRequest) {	
		Long id = scheduleCreateRequest.getId();
		StudentEntity studentEntity = findStudentById(id);
		StudentScheduleEntity StudentScheduleEntity = EntityDtoScheduleMapper.INSTANCE.studentScheduleCreateRequestToStudentScheduleEntity(scheduleCreateRequest);
		StudentScheduleEntity.setStudent(studentEntity);
		studentScheduleRepo.save(StudentScheduleEntity);
		List<SingleScheduleResponse> studentScheduleEntities = findShedulersByStudentId(id).stream().map(EntityDtoScheduleMapper.INSTANCE::studentScheduleEntityToSingleScheduleResponse)
				.collect(Collectors.toList());
		return EntityDtoScheduleMapper.INSTANCE.singleScheduleResponseToSheduleResponse(id, studentScheduleEntities);
	}
	
	@Override
	@Transactional
	public ScheduleResponse updateSchedule(ScheduleUpdateDataRequest scheduleUpdateDataRequest) {
		Long id = scheduleUpdateDataRequest.getId();
		StudentEntity studentEntity = findStudentById(id);
		StudentScheduleEntity studentScheduleEntity = findScheduleById(scheduleUpdateDataRequest.getScheduleId());
		if (scheduleUpdateDataRequest.getSheduleType() != null) {
			studentScheduleEntity.setSheduleType(scheduleUpdateDataRequest.getSheduleType());
		}
		if (scheduleUpdateDataRequest.getSendDate() != null) {
			studentScheduleEntity.setSendDate(scheduleUpdateDataRequest.getSendDate());
		}
		if (scheduleUpdateDataRequest.getSubject() != null) {
			studentScheduleEntity.setSubject(scheduleUpdateDataRequest.getSubject());
		}
		if (scheduleUpdateDataRequest.getMessage() != null) {
			studentScheduleEntity.setMessage(scheduleUpdateDataRequest.getMessage());
		}
		studentScheduleEntity.setLastModifiedDate(ZonedDateTime.now());
		studentScheduleEntity.setStudent(studentEntity);
		studentScheduleRepo.save(studentScheduleEntity);
		List<SingleScheduleResponse> studentScheduleEntities = findShedulersByStudentId(id).stream().map(EntityDtoScheduleMapper.INSTANCE::studentScheduleEntityToSingleScheduleResponse)
				.collect(Collectors.toList());
		return EntityDtoScheduleMapper.INSTANCE.singleScheduleResponseToSheduleResponse(id, studentScheduleEntities);
	}

	@Override
	@Transactional
	public Boolean deleteSchedule(Long scheduleId) {
		studentScheduleRepo.deleteById(findScheduleById(scheduleId).getScheduleId());
		return true;
	}

	@Override
	@Transactional
	public Boolean deleteAllSchedulers(Long id) {
		findShedulersByStudentId(id).forEach(schedule -> studentScheduleRepo.deleteById(schedule.getScheduleId()));
		return true;
	}

}
