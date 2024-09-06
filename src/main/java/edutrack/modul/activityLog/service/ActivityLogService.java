package edutrack.modul.activityLog.service;

import edutrack.modul.activityLog.dto.request.AddActivityLogRequest;
import edutrack.modul.activityLog.dto.response.ActivityLogResponse;

public interface ActivityLogService {

	ActivityLogResponse getStudentActivityLog(Long id);

	ActivityLogResponse addActivityLog(AddActivityLogRequest studentComment);

}
