package edutrack.group.util;

import java.time.ZonedDateTime;
import java.util.List;
import jakarta.persistence.AttributeConverter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class ListToJsonConverter implements AttributeConverter<List<ZonedDateTime>, String> {

    static final ObjectMapper MAPPER = new ObjectMapper().registerModule(new JavaTimeModule());

    @Override
    public String convertToDatabaseColumn(List<ZonedDateTime> list) {
        if (list == null)
        	{
            return null;
        	}
        try {
            return MAPPER.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ZonedDateTime> convertToEntityAttribute(String dbJson) {
        if (dbJson == null)
        	{
            return null;
        	}
        try {
            return MAPPER.readValue(dbJson, new TypeReference<List<ZonedDateTime>>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}