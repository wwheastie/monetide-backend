package org.example.monetide.uplift;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class InstantToShortDateSerializer extends JsonSerializer<Instant> {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yy")
            .withZone(ZoneId.systemDefault());

    @Override
    public void serialize(Instant instant, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        String formattedDate = formatter.format(instant);
        gen.writeString(formattedDate);
    }
}
