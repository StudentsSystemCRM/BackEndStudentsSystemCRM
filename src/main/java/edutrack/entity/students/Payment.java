package edutrack.entity.students;

import edutrack.entity.students.Student;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

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

    private String details;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;
}
