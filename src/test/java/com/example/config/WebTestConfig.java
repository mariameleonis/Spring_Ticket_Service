package com.example.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.val;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

public final class WebTestConfig {

  private WebTestConfig() {}

  public static MappingJackson2HttpMessageConverter objectMapperHttpMessageConverter() {
    MappingJackson2HttpMessageConverter converter =
        new MappingJackson2HttpMessageConverter();
    converter.setObjectMapper(objectMapper());
    return converter;
  }

  public static ObjectMapper objectMapper() {
    val objectMapper = new ObjectMapper();

    objectMapper.registerModule(new JavaTimeModule());

    val simpleModule = new SimpleModule();
    simpleModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ISO_DATE));
    objectMapper.registerModule(simpleModule);

    return objectMapper;
  }
}
