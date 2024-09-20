package edutrack.paymemt;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import edutrack.payment.controller.PaymentController;
import edutrack.payment.dto.request.AddPaymentRequest;
import edutrack.payment.dto.response.PaymentInfoResponse;
import edutrack.payment.dto.response.SinglePayment;
import edutrack.payment.service.PaymentService;
import edutrack.security.jwt.RefreshTokenFilter;
import edutrack.security.jwt.TokenGenerationFilter;
import edutrack.student.constant.LeadStatus;
import edutrack.user.repository.AccountRepository;

@WebMvcTest(controllers = PaymentController.class,
	    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
	    classes = {RefreshTokenFilter.class, TokenGenerationFilter.class}))
@AutoConfigureMockMvc(addFilters = false)
public class PaymentControllerTest {
	

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService paymentService;
    
    @MockBean
    private AccountRepository accountRepository;
    
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }
	
	@Test
    public void testAddStudentPayment() throws Exception {

        AddPaymentRequest request = new AddPaymentRequest();
        request.setStudentId(123L);
        request.setDate(LocalDate.of(2024, 8, 23));
        request.setType("tuition");
        request.setAmount(BigDecimal.valueOf(100.0));
        request.setDetails("Payment for August semester");

        PaymentInfoResponse response = new PaymentInfoResponse(
                1L,
                "John",
                "Doe",
                "123-456-7890",
                "john.doe@example.com",
                "Sample City",
                "Sample Course",
                "Sample Source",
                LeadStatus.STUDENT,
                List.of(new SinglePayment(
                		1L,
                        LocalDate.of(2024, 8, 23),
                        "tuition",
                        BigDecimal.valueOf(100.0),
                        12,
                        "Payment for August semester"
                ))
        );

        when(paymentService.addStudentPayment(any(AddPaymentRequest.class)))
                .thenReturn(response);

                mockMvc.perform(post("/api/payments/payment")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath
                                        ("$.id").value(1))
                                .andExpect(jsonPath
                                		("$.paymentInfo[0].date").value("2024-08-23"))

                                .andExpect(jsonPath
                                        ("$.paymentInfo[0].type").value("tuition"))

                                .andExpect(jsonPath(
                                        "$.paymentInfo[0].amount").value(100.0))
                                .andExpect(jsonPath(

                                        "$.paymentInfo[0].details").value("Payment for August semester"));
    }
    @Test
    public void testGetStudentPaymentInfo() throws Exception {
        Long studentId = 1L;
        LeadStatus leadStatus = LeadStatus.STUDENT;
     PaymentInfoResponse response = new PaymentInfoResponse(
                studentId,
                "John",
                "Doe",
                "123-456-7890",
                "john.doe@example.com",
                "Sample City",
                "Sample Course",
                "Sample Source",
                leadStatus,
                List.of(new SinglePayment(
                		1L,
                        LocalDate.of(2024, 8, 23),
                        "tuition",
                        BigDecimal.valueOf(100.0),
                        12,
                        "Payment for August semester"
                ))
        );

        when(paymentService.getStudentPaymentInfo(anyLong())).thenReturn(response);

                mockMvc.perform(get
                                ("/api/payments/{id}/payments", studentId)
                                .contentType
                                        ("application/json"))
                        .andExpect(status().isOk())
                .andExpect(jsonPath
                        ("$.id").value(studentId))
                .andExpect(jsonPath(

                        "$.firstName").value("John"))
                .andExpect(jsonPath
                        ("$.lastName").value("Doe"))
                        .andExpect(jsonPath
                                ("$.phoneNumber").value("123-456-7890"))
                .andExpect(jsonPath
                        ("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath
                ("$.city").value("Sample City"))
                .andExpect(jsonPath(

                "$.course").value("Sample Course"))
                .andExpect(jsonPath
                        ("$.source").value("Sample Source"))
                .andExpect(jsonPath("$.leadStatus").value("STUDENT"))
                .andExpect(jsonPath
                        ("$.paymentInfo[0].date").value("2024-08-23"))
                .andExpect(jsonPath
                        ("$.paymentInfo[0].type").value("tuition"))
                .andExpect(jsonPath("$.paymentInfo[0].amount").value(100.0))
                .andExpect(jsonPath
                        ("$.paymentInfo[0].details")
                        .value("Payment for August semester"));
    }


}
