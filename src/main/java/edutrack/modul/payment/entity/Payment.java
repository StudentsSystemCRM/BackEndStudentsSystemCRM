package edutrack.modul.payment.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

import edutrack.modul.student.entity.Student;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;
    private String type;

    @Column(precision = 15, scale = 2)
    private BigDecimal amount;

	private Integer installments;
    private String details;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;
}
