package edutrack.user.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import edutrack.user.dto.response.Role;

import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class UserEntity {
    @Id
    private String id;

    private String email;
    private String hashedPassword;
    private String name;
    private String surname;
    private String phone;
    private LocalDate birthdate;
    private LocalDate createdDate;
    private Set<Role> roles;
}
