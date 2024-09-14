package edutrack.payment.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddPaymentRequest {

	@NotNull(message = "ID cannot be null.")
	Long studentId;

	@PastOrPresent(message = "Date cannot be in the future.")
	LocalDate date;

	@NotBlank(message = "Type cannot be blank.")
	String type;

	@NotNull(message = "Amount cannot be null.")
	BigDecimal amount;

	Integer installments;
	String details;
}
