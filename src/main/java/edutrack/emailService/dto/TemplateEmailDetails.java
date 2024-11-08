package edutrack.emailService.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class TemplateEmailDetails implements Serializable {
    private static final long serialVersionUID = 1L;

    String recipient;
    String subject;
    List<String> base64Attachments;
    String template;
    Map<String, String> placeholders;
}