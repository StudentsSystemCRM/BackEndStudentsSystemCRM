package edutrack.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String whatsApp;
    private String skype;
    private String slack;
    private String status;
    private LocalDate startDate;
    private LocalDate expFinishDate;
    private LocalDate certificatesDate;

    @OneToMany(mappedBy = "group")
    private List<Student> students;

}
