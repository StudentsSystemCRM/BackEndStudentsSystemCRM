package edutrack.payment.util;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import edutrack.payment.dto.request.AddPaymentRequest;
import edutrack.payment.dto.response.PaymentInfoResponse;
import edutrack.payment.dto.response.SinglePayment;
import edutrack.payment.entity.PaymentEntity;
import edutrack.student.entity.StudentEntity;

@Mapper
public interface EntityDtoPaymentMapper {
	
	EntityDtoPaymentMapper INSTANCE = Mappers.getMapper(EntityDtoPaymentMapper.class);
	
	@Mapping(target = "paymentInfo", source = "paymentsResp")
    PaymentInfoResponse studentToPaymentInfoResponse(StudentEntity student, List<SinglePayment> paymentsResp);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "student", source = "student")
    PaymentEntity addPaymentRequestToPayment(AddPaymentRequest addPaymentRequest, StudentEntity student);
    
    SinglePayment paymentToSinglePayment(PaymentEntity payment);

}
