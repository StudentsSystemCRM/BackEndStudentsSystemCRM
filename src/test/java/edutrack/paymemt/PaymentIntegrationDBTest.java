package edutrack.paymemt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.jdbc.Sql;

import edutrack.configuration.ElasticsearchDeleteDataScheduler;
import edutrack.payment.dto.request.AddPaymentRequest;
import edutrack.payment.dto.response.PaymentInfoResponse;
import edutrack.payment.dto.response.SinglePayment;
import edutrack.payment.entity.PaymentEntity;
import edutrack.payment.repository.PaymentRepository;
import edutrack.payment.service.PaymentService;
import edutrack.student.repository.StudentRepository;
import edutrack.student.service.StudentService;
import edutrack.user.repository.AccountRepository;

@SpringBootTest(properties = {
		"mailgun.api.key=disabled",
		"mailgun.domain=disabled",
		"mailgun.api.base-url=disabled",
		"mailgun.from-email=disabled",
		"mailgun.signature=disabled"
})
@Disabled
@Sql(scripts = { "classpath:testdata.sql" })
public class PaymentIntegrationDBTest {
	
	@MockBean
	ElasticsearchDeleteDataScheduler dataScheduler;
	
	@Mock
	AccountRepository userRepository;

	@Mock
	PasswordEncoder passwordEncoder;

	@Mock
	Authentication authentication;

	@Mock
	SecurityContext securityContext;

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
        assertEquals(0, resp.getPaymentInfo().get(0).getAmount().compareTo(BigDecimal.valueOf(1500.00)));
	}

	@Test
	public void testCascadeDeleteStudent() {
		AddPaymentRequest paymentRequest = new AddPaymentRequest(STUDENT_ID_DB_H2, LocalDate.now(), "Cash",
				BigDecimal.valueOf(2000.00), 2, "Training Fee");
		paymentService.addStudentPayment(paymentRequest);

		List<PaymentEntity> payments = paymentRepo.findByStudentId(STUDENT_ID_DB_H2);
		assertFalse(payments.isEmpty());

		studentService.deleteStudent(STUDENT_ID_DB_H2);

		assertFalse(studentRepo.findById(STUDENT_ID_DB_H2).isPresent());

		payments = paymentRepo.findByStudentId(STUDENT_ID_DB_H2);
		assertTrue(payments.isEmpty());
	}

}
