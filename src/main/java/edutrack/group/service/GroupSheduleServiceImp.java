package edutrack.group.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import edutrack.group.constant.GroupStatus;
import edutrack.group.dto.request.GroupUpdateDataRequest;
import edutrack.group.dto.response.GroupDataResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class GroupSheduleServiceImp implements GroupSheduleService{

	@Autowired
	GroupServiceImp groupServiceImp;
	
	@Override
	@Scheduled(cron = "0 0 2 * * MON-SUN", zone = "Asia/Jerusalem") // every day of week at 2:00:00 - "0 0 2 * * MON-SUN", ${cron.expression}
	public void scheduleDeactivateAfter30Days() {
		List<GroupDataResponse> groupResponse = groupServiceImp.getGroupsByStatus(GroupStatus.ACTIVE);
		LocalDate date = LocalDate.now();
		groupResponse.forEach(groupDataResponse -> {
			if(groupDataResponse.getDeactivateAfter30Days()!=null && groupDataResponse.getDeactivateAfter30Days()) 
    			{
    				if(groupDataResponse.getExpFinishDate()!=null && groupDataResponse.getExpFinishDate().plusDays(30).isEqual(date)) 
    				{
    					GroupUpdateDataRequest groupRequest = new GroupUpdateDataRequest();
    					groupRequest.setId(groupDataResponse.getId());
    					groupRequest.setStatus(GroupStatus.INACTIVE);
    					groupRequest.setDeactivateAfter30Days(false);
    					groupServiceImp.updateGroup(groupRequest);
    				}
    			}
    		});
       }

}
