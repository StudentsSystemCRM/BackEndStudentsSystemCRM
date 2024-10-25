package edutrack.group.entity;

import edutrack.group.constant.GroupStatus;
import edutrack.group.util.ListToJsonConverter;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import edutrack.schedule.entity.GroupScheduleEntity;
import edutrack.student.entity.StudentEntity;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "groups")
public class GroupEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name")
    private String name;

    @Column(name = "whats_app")
    private String whatsApp;
    private String skype;
    private String slack;
    
    @Enumerated(EnumType.STRING)
    private GroupStatus status;
    
    private LocalDate startDate;   
    private LocalDate expFinishDate;  
    @Column(name = "deactivate_after_30_days")
    private Boolean deactivateAfter30Days;
    
    @ManyToMany
    @JoinTable(
        name = "groups_students",
        joinColumns = @JoinColumn(name = "group_id"),
        inverseJoinColumns = @JoinColumn(name = "students_id")
    )
    private Set<StudentEntity> students  = new HashSet<>();
    
    @OneToMany(mappedBy = "group")
    @CollectionTable(name = "group_schedulers")
    private List<GroupScheduleEntity> groupSchedulers = new ArrayList<>();
    
    @Column(columnDefinition = "json", name = "lessons_days")
	@Convert(converter = ListToJsonConverter.class)
    private List<ZonedDateTime> lessonsDays = new ArrayList<>();
    
    @Column(columnDefinition = "json", name = "webinars_days")
	@Convert(converter = ListToJsonConverter.class)
    private List<ZonedDateTime> webinarsDays = new ArrayList<>();
    
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
