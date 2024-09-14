package edutrack.user.dto.request;

import edutrack.user.dto.validation.ValidRole;
import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserRoleRequest {
	@ValidRole
	String role;
}
