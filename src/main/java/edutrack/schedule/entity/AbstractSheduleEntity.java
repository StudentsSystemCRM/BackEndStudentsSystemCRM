package edutrack.schedule.entity;

import java.time.ZonedDateTime;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import edutrack.schedule.constant.SheduleType;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@MappedSuperclass
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractSheduleEntity {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long scheduleId;
    @Enumerated(EnumType.STRING)
    SheduleType sheduleType;
    ZonedDateTime sendDate;
    String subject;
    String message;
    
    @CreatedDate
    @Column(name = "created_date") 
    ZonedDateTime createdDate;
    @CreatedBy
    @Column(name = "created_by") 
    String createdBy;
    @LastModifiedDate
    @Column(name = "updated_date") 
    ZonedDateTime lastModifiedDate; 
    @LastModifiedBy 
    @Column(name = "updated_by")
    String lastModifiedBy;
}
