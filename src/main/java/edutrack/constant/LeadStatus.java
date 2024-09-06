package edutrack.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LeadStatus {
    LEAD("Lead"),
    IN_WORK("In work"),
    CONSULTATION("Consultation"),
    SAVE_FOR_LATER("Save for later"),
    STUDENT("Student"),
    ARCHIVE("Archive");

    private final String status;
}