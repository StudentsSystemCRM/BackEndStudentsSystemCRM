package edutrack.group.util;

import java.time.LocalDateTime;
import java.util.List;
import jakarta.persistence.AttributeConverter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class ListToJsonConverter implements AttributeConverter<List<LocalDateTime>, String> {

    static final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Override
    public String convertToDatabaseColumn(List<LocalDateTime> list) {
        if (list == null)
            return null;
        try {
            return mapper.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<LocalDateTime> convertToEntityAttribute(String dbJson) {
        if (dbJson == null)
            return null;
        try {
            return mapper.readValue(dbJson, new TypeReference<List<LocalDateTime>>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}