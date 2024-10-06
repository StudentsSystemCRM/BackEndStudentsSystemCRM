package edutrack.user.dto.validation;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import edutrack.user.exception.InvalidDateFormatException;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

public class MultiFormatLocalDateDeserializer extends JsonDeserializer<LocalDate> {
    private static final List<DateTimeFormatter> DATE_FORMATTERS = Arrays.asList(
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("MM-dd-yyyy"),
            DateTimeFormatter.ofPattern("dd.MM.yyyy")
    );

    @Override
    public LocalDate deserialize(JsonParser p, DeserializationContext context) throws IOException {
        String date = p.getText();

        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                return LocalDate.parse(date, formatter);
            } catch (DateTimeParseException ignored) {
            	
            }
        }

        throw new InvalidDateFormatException
                ("Invalid date format. Please use one of the following formats: yyyy-MM-dd, dd/MM/yyyy, MM-dd-yyyy, dd.MM.yyyy");
    }
}
