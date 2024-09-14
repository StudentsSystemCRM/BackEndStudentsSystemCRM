package edutrack.lecturer.entity;

import edutrack.group.entity.GroupEntity;
import edutrack.lecturer.constant.LecturerStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "lecturers")
public class LecturerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;
    private String city;
    @Enumerated(EnumType.STRING)
    private LecturerStatus status;

    @ManyToMany
    @JoinTable(
            name = "lecturer_group",
            joinColumns = @JoinColumn(name = "lecturer_id"),
            inverseJoinColumns = @JoinColumn(name = "group_id"))
    private Set<GroupEntity> groups;
    
    @CreatedBy
    @Column(name = "created_by") 
    String createdBy; 
    @LastModifiedBy 
    @Column(name = "updated_by")
    String lastModifiedBy;

}
