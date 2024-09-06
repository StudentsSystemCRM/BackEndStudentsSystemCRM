package edutrack.modul.reminder.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import edutrack.modul.group.entity.Group;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "group_reminders")
public class GroupReminders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;
    private String information;

    @ManyToOne
    @JoinColumn(name = "group_name")
    private Group group;
}
