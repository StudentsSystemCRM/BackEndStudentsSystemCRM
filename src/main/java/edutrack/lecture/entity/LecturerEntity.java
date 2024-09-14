package edutrack.lecture.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

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
    private String status;
    
    @CreatedBy
    @Column(name = "created_by") 
    String createdBy; 
    @LastModifiedBy 
    @Column(name = "updated_by")
    String lastModifiedBy;

}
