package edutrack.modul.payment.service;

import edutrack.modul.payment.dto.request.AddPaymentRequest;
import edutrack.modul.payment.dto.response.PaymentInfoResponse;

public interface PaymentService {
	
	PaymentInfoResponse getStudentPaymentInfo(Long id);
	PaymentInfoResponse addStudentPayment(AddPaymentRequest studentPayment);

}
