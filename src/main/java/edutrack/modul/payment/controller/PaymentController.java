package edutrack.modul.payment.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edutrack.modul.payment.dto.request.AddPaymentRequest;
import edutrack.modul.payment.dto.response.PaymentInfoResponse;
import edutrack.modul.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentController {
	
	PaymentService paymentService;
	
	
    @GetMapping("/{id}/payments")
    @Operation(summary = "Get a student's payments by ID", description = "Provide an ID to lookup a specific student's payments.")
    public PaymentInfoResponse getStudentPaymentInfo(@PathVariable Long id) {
        return paymentService.getStudentPaymentInfo(id);
    }

    @PostMapping("/payment")
    @Operation(summary = "Add information about the student's payment history.", description = "Provide the necessary data to create a new payment for a specific student.")
    public PaymentInfoResponse addStudentPayment(@RequestBody @Valid AddPaymentRequest studentPayment) {
        return paymentService.addStudentPayment(studentPayment);
    }
}
