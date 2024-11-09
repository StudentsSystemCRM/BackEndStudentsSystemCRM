package edutrack.emailService.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "EmailStatus")
public class EmailStatusEntity {
    @Id
    String messageId;
    String status;
    Timestamp timestamp;
    String recipient;
}
