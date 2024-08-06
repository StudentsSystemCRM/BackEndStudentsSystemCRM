package edutrack.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class ActivityLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;
    private String information;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

}
