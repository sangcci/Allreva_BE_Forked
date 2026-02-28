package com.backend.allreva.util.converter;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractJsonSetConverter<T> implements AttributeConverter<Set<T>, String> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
            .setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE)
            .setVisibility(PropertyAccessor.IS_GETTER, JsonAutoDetect.Visibility.NONE);

    private final Class<T> elementType;

    protected AbstractJsonSetConverter(final Class<T> elementType) {
        this.elementType = elementType;
    }

    @Override
    public String convertToDatabaseColumn(final Set<T> set) {
        if (set == null || set.isEmpty()) {
            return "[]";
        }
        try {
            return OBJECT_MAPPER.writeValueAsString(set);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Set을 JSON으로 변환하는 데 실패했습니다.", e);
        }
    }

    @Override
    public Set<T> convertToEntityAttribute(final String json) {
        if (json == null || json.isBlank()) {
            return new HashSet<>();
        }
        try {
            return OBJECT_MAPPER.readValue(
                    json, OBJECT_MAPPER.getTypeFactory().constructCollectionType(Set.class, elementType));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON을 Set으로 변환하는 데 실패했습니다.", e);
        }
    }
}
