package edutrack.student.entity;

import edutrack.student.constant.LeadStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import edutrack.activityLog.entity.ActivityLogEntity;
import edutrack.group.entity.GroupEntity;
import edutrack.payment.entity.PaymentEntity;
import edutrack.schedule.entity.StudentScheduleEntity;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "students")
public class StudentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;
    private String city;
    private String course;
    private String source;
    @Enumerated(EnumType.STRING)
    private LeadStatus leadStatus;
    private Long originalGroupId;
    private Integer totalSumToPay;

    @ManyToMany(mappedBy = "students")
    private List<GroupEntity> groups;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    private List<ActivityLogEntity> activityLogs = new ArrayList<>();

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    private List<PaymentEntity> payments = new ArrayList<>();
    
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    @CollectionTable(name = "student_schedulers")
    private List<StudentScheduleEntity> studentSchedulers = new ArrayList<>();
    
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
