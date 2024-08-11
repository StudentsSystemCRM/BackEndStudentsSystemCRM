package edutrack.entity.students;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;
    private String details;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;
}
