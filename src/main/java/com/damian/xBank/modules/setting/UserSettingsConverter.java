package com.damian.xBank.modules.setting;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.io.IOException;

@Converter
public class UserSettingsConverter implements AttributeConverter<UserSettings, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(UserSettings settings) {
        try {
            return objectMapper.writeValueAsString(settings);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error converting settings to JSON", e);
        }
    }

    @Override
    public UserSettings convertToEntityAttribute(String json) {
        try {
            return objectMapper.readValue(json, UserSettings.class);
        } catch (IOException e) {
            throw new IllegalArgumentException("Error reading JSON settings", e);
        }
    }
}