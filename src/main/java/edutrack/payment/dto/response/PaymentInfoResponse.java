package edutrack.payment.dto.response;

import java.util.List;

import edutrack.student.constant.LeadStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentInfoResponse {
	
	Long id;
	String firstName;
	String lastName;
	String phoneNumber;
	String email;
	String city;
	String course;
	String source;
    LeadStatus leadStatus;
	List<SinglePayment> paymentInfo;

}
