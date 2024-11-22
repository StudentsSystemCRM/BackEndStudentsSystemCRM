package edutrack.schedule.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import edutrack.group.entity.GroupEntity;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "group_schedulers")
public class GroupScheduleEntity extends AbstractSheduleEntity {

    @ManyToOne
    @JoinColumn(name = "group_id")
    private GroupEntity group;

}
