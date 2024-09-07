package edutrack.modul.group.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

import edutrack.constant.GroupStatus;
import edutrack.constant.WeekDay;
import edutrack.modul.reminder.entity.GroupReminders;
import edutrack.modul.student.entity.Student;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "groups")
public class Group {
    @Id
    @Column(name = "name",unique=true)
    private String name;

    private String whatsApp;
    private String skype;
    private String slack;
    @Enumerated(EnumType.STRING)
    private GroupStatus status;
    private LocalDate startDate;
    private LocalDate expFinishDate;
    private LocalDate DeactivateAfter30Days;
    
    @ElementCollection
    private List<Student> students  = new ArrayList<>();
    
    @OneToMany(mappedBy = "group")
    private List<GroupReminders> groupReminders = new ArrayList<>();
    
    @ElementCollection
    @Enumerated(EnumType.STRING)
    private List<WeekDay> lessonsDays = new ArrayList<>();
    
    @ElementCollection
    @Enumerated(EnumType.STRING)
    private List<WeekDay> webinarsDays = new ArrayList<>();
    
    @CreatedBy
    @Column(name = "created_by") 
    String createdBy; 
    @LastModifiedBy 
    @Column(name = "updated_by")
    String lastModifiedBy;
}
