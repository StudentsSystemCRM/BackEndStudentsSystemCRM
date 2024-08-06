package edutrack.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "Test")
@Entity
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TestEntity {
    @Id
    Integer id;
    String test;
}
