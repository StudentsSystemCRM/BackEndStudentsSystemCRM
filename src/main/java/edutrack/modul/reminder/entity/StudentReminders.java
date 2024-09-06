package edutrack.modul.reminder.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import edutrack.modul.student.entity.Student;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "student_reminders")
public class StudentReminders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;
    private String information;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;
}
