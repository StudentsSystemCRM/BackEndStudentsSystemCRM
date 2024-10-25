package edutrack.schedule.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import edutrack.student.entity.StudentEntity;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "student_schedulers")
public class StudentScheduleEntity extends AbstractSheduleEntity {

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private StudentEntity student;

}
