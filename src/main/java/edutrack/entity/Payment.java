package edutrack.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
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
