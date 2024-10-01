package edutrack.activityLog;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import edutrack.activityLog.dto.request.AddActivityLogRequest;
import edutrack.activityLog.dto.response.ActivityLogResponse;
import edutrack.activityLog.service.ActivityLogService;

@SpringBootTest
@Sql(scripts = {"classpath:testdata.sql"})
public class ActivityLogIntegrationDBTest {
	 
    @Autowired
    private ActivityLogService activityLogService;
  
    static final Long STUDENT_ID_DB_H2 = 2L;
      
    @Test
    public void testAddStudentComment() {

        AddActivityLogRequest commentRequest = new AddActivityLogRequest(STUDENT_ID_DB_H2, LocalDate.now(), "Second comment");
        ActivityLogResponse activityLogResponse = activityLogService.addActivityLog(commentRequest);
        
        assertNotNull(activityLogResponse);
        assertEquals(1, activityLogResponse.getActivityLogs().size());
        assertEquals("Second comment", activityLogResponse.getActivityLogs().get(0).getMessage());
    }

}
