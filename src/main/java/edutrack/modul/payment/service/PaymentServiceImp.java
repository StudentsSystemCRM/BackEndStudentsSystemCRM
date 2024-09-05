package edutrack.modul.payment.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import edutrack.exception.StudentNotFoundException;
import edutrack.modul.payment.dto.request.AddPaymentRequest;
import edutrack.modul.payment.dto.response.PaymentInfoResponse;
import edutrack.modul.payment.dto.response.SinglePayment;
import edutrack.modul.payment.entity.Payment;
import edutrack.modul.payment.repository.PaymentRepository;
import edutrack.modul.student.entity.Student;
import edutrack.modul.student.repository.StudentRepository;
import edutrack.util.EntityDtoMapper;
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
		Student student = studentRepo.findById(id)
				.orElseThrow(() -> new StudentNotFoundException("Student with id " + id + " not found"));
		return 	EntityDtoMapper.INSTANCE.studentToPaymentInfoResponse(student, getListOfSinglePaymentsById(id));
	}


	@Override
	@Transactional
	public PaymentInfoResponse addStudentPayment(AddPaymentRequest studentPayment) {
        Student student = studentRepo.findById(studentPayment.getStudentId())
				.orElseThrow(() -> new StudentNotFoundException("Student with id " + studentPayment.getStudentId() + " not found"));
        Payment savedPayment = EntityDtoMapper.INSTANCE.addPaymentRequestToPayment(studentPayment,student);
        savedPayment = paymentRepo.save(savedPayment);
        return EntityDtoMapper.INSTANCE.studentToPaymentInfoResponse(student,  getListOfSinglePaymentsById(studentPayment.getStudentId()));
	}
	

	private List<SinglePayment> getListOfSinglePaymentsById(Long id) {
		List<Payment> payments = paymentRepo.findByStudentId(id);
		List<SinglePayment> paymentsResp = (payments != null) 
			    ? payments.stream()
			              .map(EntityDtoMapper.INSTANCE::paymentToSinglePayment)
			              .collect(Collectors.toList())
			    : Collections.emptyList();
		return paymentsResp;
	}

}
