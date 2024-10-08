package com.tiger.cores.configs.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.tiger.cores.utils.JsonUtil;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
public class JacksonConfig {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        return JsonUtil.objectMapper();
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
        return builder -> {
            builder.serializerByType(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ISO_DATE_TIME));
            builder.deserializerByType(
                    LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ISO_DATE_TIME));
        };
    }

    public String convertToUserTimeZone(LocalDateTime dateTime, String userTimeZone) {
        ZonedDateTime utcTime = dateTime.atZone(ZoneOffset.UTC);
        ZonedDateTime userTime = utcTime.withZoneSameInstant(ZoneId.of(userTimeZone));
        return userTime.format(DateTimeFormatter.ISO_DATE_TIME);
    }
}
