package edutrack.paymemt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import edutrack.modul.payment.dto.request.AddPaymentRequest;
import edutrack.modul.payment.dto.response.PaymentInfoResponse;
import edutrack.modul.payment.dto.response.SinglePayment;
import edutrack.modul.payment.entity.Payment;
import edutrack.modul.payment.repository.PaymentRepository;
import edutrack.modul.payment.service.PaymentService;
import edutrack.modul.student.repository.StudentRepository;
import edutrack.modul.student.service.StudentService;

@SpringBootTest
@Sql(scripts = { "classpath:testdata.sql" })
public class PaymentIntegretionDBTest {

	@Autowired
	private PaymentService paymentService;
	
	@Autowired
	private StudentRepository studentRepo;
	
	@Autowired
    private StudentService studentService;

	@Autowired
	private PaymentRepository paymentRepo;

	static final Long STUDENT_ID_DB_H2 = 2L;

	@Test
	public void testAddStudentPayment() {
		AddPaymentRequest paymentRequest = new AddPaymentRequest(STUDENT_ID_DB_H2, LocalDate.now(), "Credit Card",
				BigDecimal.valueOf(1500.00), 3, "Course Fee");

		PaymentInfoResponse payments = paymentService.addStudentPayment(paymentRequest);
		SinglePayment paymentResponse = payments.getPaymentInfo().get(0);
		PaymentInfoResponse resp = paymentService.getStudentPaymentInfo(STUDENT_ID_DB_H2);

		assertNotNull(paymentResponse.getId());
		assertEquals(BigDecimal.valueOf(1500.00).floatValue(), paymentResponse.getAmount().floatValue());
		assertEquals("Course Fee", paymentResponse.getDetails());
		assertEquals(resp.getPaymentInfo().size(), 1);
		assertTrue(resp.getPaymentInfo().get(0).getAmount().compareTo(BigDecimal.valueOf(1500.00)) == 0);
	}

	@Test
	public void testCascadeDeleteStudent() {
		AddPaymentRequest paymentRequest = new AddPaymentRequest(STUDENT_ID_DB_H2, LocalDate.now(), "Cash",
				BigDecimal.valueOf(2000.00), 2, "Training Fee");
		paymentService.addStudentPayment(paymentRequest);

		List<Payment> payments = paymentRepo.findByStudentId(STUDENT_ID_DB_H2);
		assertFalse(payments.isEmpty());

		studentService.deleteStudent(STUDENT_ID_DB_H2);

		assertFalse(studentRepo.findById(STUDENT_ID_DB_H2).isPresent());

		payments = paymentRepo.findByStudentId(STUDENT_ID_DB_H2);
		assertTrue(payments.isEmpty());
	}

}
