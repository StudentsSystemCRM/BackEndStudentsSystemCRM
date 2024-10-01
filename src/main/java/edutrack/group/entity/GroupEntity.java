package edutrack.group.entity;

import edutrack.group.constant.GroupStatus;
import edutrack.group.constant.WeekDay;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

import edutrack.schedule.entity.GroupScheduleEntity;
import edutrack.student.entity.StudentEntity;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "groups")
public class GroupEntity {
	@Id
	@Column(name = "name", unique = true)
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
	private LocalDate deactivateAfter30Days;

	@ManyToMany
	@JoinTable(name = "groups_students", joinColumns = @JoinColumn(name = "group_name"), inverseJoinColumns = @JoinColumn(name = "students_id"))
	private List<StudentEntity> students = new ArrayList<>();

	@OneToMany(mappedBy = "group")
	private List<GroupScheduleEntity> groupReminders = new ArrayList<>();

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
