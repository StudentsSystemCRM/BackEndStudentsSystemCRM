package edutrack.payment.service;

import edutrack.payment.dto.request.AddPaymentRequest;
import edutrack.payment.dto.response.PaymentInfoResponse;

public interface PaymentService {
	
	PaymentInfoResponse getStudentPaymentInfo(Long id);
	PaymentInfoResponse addStudentPayment(AddPaymentRequest studentPayment);

}
