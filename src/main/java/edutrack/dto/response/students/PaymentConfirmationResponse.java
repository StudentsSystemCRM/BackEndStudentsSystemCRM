package edutrack.dto.response.students;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentConfirmationResponse {

    private Long id;
    private LocalDate date;

    private String type;
    private BigDecimal amount;
    private String details;
}