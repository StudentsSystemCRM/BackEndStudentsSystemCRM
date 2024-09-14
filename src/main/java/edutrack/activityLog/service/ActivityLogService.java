package edutrack.activityLog.service;

import edutrack.activityLog.dto.response.ActivityLogResponse;
import edutrack.activityLog.dto.request.AddActivityLogRequest;

public interface ActivityLogService {

	ActivityLogResponse getStudentActivityLog(Long id);

	ActivityLogResponse addActivityLog(AddActivityLogRequest studentComment);

}
