package edutrack.schedule.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import edutrack.schedule.constant.SheduleType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ScheduledUpdates{

    @Autowired
    SimpMessagingTemplate template;
    
    @Autowired
    GroupScheduleService groupScheduleService;

//    @Scheduled(fixedDelay=60000)
    public void publishUpdates(){
        template.convertAndSend("/topic/reminders", groupScheduleService.getSchedulersBySheduleType(SheduleType.LOCAL));
    }
}
