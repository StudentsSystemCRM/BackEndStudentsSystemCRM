package edutrack.util;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import edutrack.modul.activityLog.dto.response.ActivityLogResponse;
import edutrack.modul.activityLog.dto.response.SingleActivityLog;
import edutrack.modul.activityLog.entity.ActivityLog;
import edutrack.modul.group.dto.request.GroupCreateRequest;
import edutrack.modul.group.entity.Group;
import edutrack.modul.payment.dto.request.AddPaymentRequest;
import edutrack.modul.payment.dto.response.PaymentInfoResponse;
import edutrack.modul.payment.dto.response.SinglePayment;
import edutrack.modul.payment.entity.Payment;
import edutrack.modul.reminder.dto.response.ReminderResponse;
import edutrack.modul.reminder.dto.response.SingleReminder;
import edutrack.modul.student.dto.request.StudentCreateRequest;
import edutrack.modul.student.dto.response.StudentDataResponse;
import edutrack.modul.student.entity.Student;
import edutrack.modul.user.dto.request.UserRegisterRequest;
import edutrack.modul.user.dto.response.LoginSuccessResponse;
import edutrack.modul.user.dto.response.UserDataResponse;
import edutrack.modul.user.entity.Account;

@Mapper
public interface EntityDtoMapper {
	EntityDtoMapper INSTANCE = Mappers.getMapper(EntityDtoMapper.class);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "hashedPassword", ignore = true)
	@Mapping(target = "createdDate", expression = "java(java.time.LocalDate.now())")
	@Mapping(target = "roles", ignore = true)
	Account userRegisterRequestToUser(UserRegisterRequest userRegisterRequest);

	@Mapping(target = "token", ignore = true)
	LoginSuccessResponse userToLoginSuccessResponse(Account user);
	
	UserDataResponse userToUserDataResponse(Account user);

	//student
    @Mapping(source = "name", target = "firstName")
    @Mapping(source = "surname", target = "lastName")
    @Mapping(source = "phone", target = "phoneNumber")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "activityLogs", ignore = true)
    @Mapping(target = "payments", ignore = true)
    @Mapping(target = "groups", ignore = true)
    @Mapping(target = "studentReminders", ignore = true)
    @Mapping(target = "totalSumToPay", ignore = true)
    @Mapping(target = "originalGroup", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
	Student studentCreateRequestToStudent(StudentCreateRequest studentCreate);

    @Mapping(source = "firstName", target = "name")
    @Mapping(source = "lastName", target = "surname")
    @Mapping(source = "phoneNumber", target = "phone")
	StudentDataResponse studentToStudentDataResponse(Student studentEntity);

    @Mapping(source = "information", target = "message")
	SingleActivityLog activityLogEntitytoStudentActivityLog(ActivityLog activityLog);
    
    @Mapping(target = "groupReminders", ignore = true)
    @Mapping(target = "deactivateAfter30Days", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    Group groupCreateRequestToGroup(GroupCreateRequest groupCreate);
    
    //payment
    @Mapping(target = "paymentInfo", source = "paymentsResp")
    PaymentInfoResponse studentToPaymentInfoResponse(Student student, List<SinglePayment> paymentsResp);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "student", source = "student")
    Payment addPaymentRequestToPayment(AddPaymentRequest addPaymentRequest, Student student);
    
    SinglePayment paymentToSinglePayment(Payment payment);
    
    //activityLog
    @Mapping(target = "activityLogs", source = "studentActivityLog")
    ActivityLogResponse studentToActivityLogResponse(Student student, List<SingleActivityLog> studentActivityLog);
    
    //reminders
    @Mapping(target = "reminders", source = "studentReminders")
    ReminderResponse studentToReminderResponse(Student student, List<SingleReminder> studentReminders);
}
