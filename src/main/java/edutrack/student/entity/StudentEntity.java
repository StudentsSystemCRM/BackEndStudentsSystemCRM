package edutrack.student.entity;

import edutrack.student.constant.LeadStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

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
    private String originalGroup;
    private Integer totalSumToPay;

    @ManyToMany(mappedBy = "students")
    private List<GroupEntity> groups;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    private List<ActivityLogEntity> activityLogs = new ArrayList<>();

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    private List<PaymentEntity> payments = new ArrayList<>();
    
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    private List<StudentScheduleEntity> studentReminders = new ArrayList<>();
     
    @CreatedBy
    @Column(name = "created_by") 
    String createdBy; 
    @LastModifiedBy 
    @Column(name = "updated_by")
    String lastModifiedBy;
}
