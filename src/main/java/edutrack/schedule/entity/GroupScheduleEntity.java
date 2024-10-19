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

import edutrack.group.entity.GroupEntity;
import edutrack.schedule.constant.SheduleType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "group_shedulers")
public class GroupScheduleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private SheduleType sheduleType;
    private LocalDateTime sendDate;
    private String subject;
    private String message;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private GroupEntity group;
    
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
