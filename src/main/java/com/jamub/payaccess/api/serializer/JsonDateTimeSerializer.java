package com.jamub.payaccess.api.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class JsonDateTimeSerializer extends JsonSerializer<LocalDateTime> {

    @Override
    public void serialize(LocalDateTime localDateTime, JsonGenerator gen, SerializerProvider provider) throws IOException {
        ZonedDateTime utcDateTime = this.localDateTimeToUtcDateTime(localDateTime, ZoneId.systemDefault());
        gen.writeString(utcDateTime.format(DateTimeFormatter.ISO_DATE_TIME));
    }


    public static ZonedDateTime localDateTimeToUtcDateTime(LocalDateTime dateTime, ZoneId zoneId) {
        ZonedDateTime zonedDateTime;
        if (dateTime == null || zoneId == null) {
            zonedDateTime = null;
        } else {
            zonedDateTime = ZonedDateTime.of(dateTime, zoneId)
                    .withZoneSameInstant(ZoneOffset.UTC);
        }
        return zonedDateTime;
    }
}