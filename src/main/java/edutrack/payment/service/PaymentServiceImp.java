package edutrack.payment.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import edutrack.payment.dto.response.PaymentInfoResponse;
import edutrack.payment.dto.response.SinglePayment;
import edutrack.payment.entity.PaymentEntity;
import org.springframework.stereotype.Service;

import edutrack.exception.StudentNotFoundException;
import edutrack.payment.dto.request.AddPaymentRequest;
import edutrack.payment.repository.PaymentRepository;
import edutrack.payment.util.EntityDtoPaymentMapper;
import edutrack.student.entity.StudentEntity;
import edutrack.student.repository.StudentRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class PaymentServiceImp implements PaymentService {

	StudentRepository studentRepo;
	PaymentRepository paymentRepo;
	
	@Override
	@Transactional
	public PaymentInfoResponse getStudentPaymentInfo(Long id) {
		StudentEntity student = studentRepo.findById(id)
				.orElseThrow(() -> new StudentNotFoundException("Student with id " + id + " not found"));
		return 	EntityDtoPaymentMapper.INSTANCE.studentToPaymentInfoResponse(student, getListOfSinglePaymentsById(id));
	}

	@Override
	@Transactional
	public PaymentInfoResponse addStudentPayment(AddPaymentRequest studentPayment) {
        StudentEntity student = studentRepo.findById(studentPayment.getStudentId())
				.orElseThrow(() -> new StudentNotFoundException("Student with id " + studentPayment.getStudentId() + " not found"));
        PaymentEntity savedPayment = EntityDtoPaymentMapper.INSTANCE.addPaymentRequestToPayment(studentPayment,student);
        savedPayment = paymentRepo.save(savedPayment);
        return EntityDtoPaymentMapper.INSTANCE.studentToPaymentInfoResponse(student,  getListOfSinglePaymentsById(studentPayment.getStudentId()));
	}
	
	private List<SinglePayment> getListOfSinglePaymentsById(Long id) {
		List<PaymentEntity> payments = paymentRepo.findByStudentId(id);
		List<SinglePayment> paymentsResp = (payments != null) 
			    ? payments.stream()
			              .map(EntityDtoPaymentMapper.INSTANCE::paymentToSinglePayment)
			              .collect(Collectors.toList())
			    : Collections.emptyList();
		return paymentsResp;
	}

}
