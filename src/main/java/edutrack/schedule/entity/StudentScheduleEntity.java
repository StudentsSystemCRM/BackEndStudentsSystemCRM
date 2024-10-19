package edutrack.schedule.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import edutrack.schedule.constant.SheduleType;
import edutrack.student.entity.StudentEntity;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "student_shedulers")
public class StudentScheduleEntity {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private SheduleType sheduleType;
    private LocalDateTime sendDate;
    private String subject;
    private String message;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private StudentEntity student;
    
    @CreatedDate
    @Column(name = "created_date") 
    LocalDateTime createdDate;
    @CreatedBy
    @Column(name = "created_by") 
    String createdBy;
    @LastModifiedDate
    @Column(name = "updated_date") 
    LocalDateTime lastModifiedDate; 
    @LastModifiedBy 
    @Column(name = "updated_by")
    String lastModifiedBy;
}
