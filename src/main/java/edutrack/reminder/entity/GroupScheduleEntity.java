package edutrack.reminder.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import edutrack.group.entity.GroupEntity;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "group_reminders")
public class GroupScheduleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dateTime;
    private String comment;

    @ManyToOne
    @JoinColumn(name = "group_name")
    private GroupEntity group;
}
