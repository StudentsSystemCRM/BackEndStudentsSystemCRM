package edutrack.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GroupStatus {
    ACTIVE("Active"),
    INACTIVE("Inactive");

    private final String groupStatus;
}
