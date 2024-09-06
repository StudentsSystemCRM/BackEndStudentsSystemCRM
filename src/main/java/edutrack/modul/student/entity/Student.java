package edutrack.modul.student.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

import edutrack.modul.activityLog.entity.ActivityLog;
import edutrack.constant.LeadStatus;
import edutrack.modul.group.entity.Group;
import edutrack.modul.payment.entity.Payment;
import edutrack.modul.reminder.entity.StudentReminders;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "students")
public class Student {
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

    @ManyToOne
    @JoinColumn(name = "group_name")
    private Group group;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    private List<ActivityLog> activityLogs = new ArrayList<>();

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    private List<Payment> payments = new ArrayList<>();
    
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    private List<StudentReminders> studentReminders = new ArrayList<>();
     
    @CreatedBy
    @Column(name = "created_by") 
    String createdBy; 
    @LastModifiedBy 
    @Column(name = "updated_by")
    String lastModifiedBy;
}
